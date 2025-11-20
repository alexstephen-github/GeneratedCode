from django.test import TestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase, APIClient
from django.contrib.auth.models import User

from .models import Product
from .serializers import ProductSerializer

# --- Model Tests ---
class ProductModelTest(TestCase):
    """
    Tests for the Product model.
    """
    def setUp(self):
        """
        Set up non-modified objects used by all test methods.
        """
        self.product1 = Product.objects.create(
            name="Test Product 1",
            description="Description for test product 1",
            price=19.99,
            is_available=True
        )
        self.product2 = Product.objects.create(
            name="Test Product 2",
            description="Another product",
            price=120.50,
            is_available=False
        )

    def test_product_creation(self):
        """
        Ensure product can be created and properties are correct.
        """
        self.assertEqual(Product.objects.count(), 2)
        self.assertEqual(self.product1.name, "Test Product 1")
        self.assertEqual(float(self.product1.price), 19.99) # Convert Decimal to float for comparison
        self.assertTrue(self.product1.is_available)
        self.assertIsNotNone(self.product1.created_at)
        self.assertIsNotNone(self.product1.updated_at)

    def test_product_str_representation(self):
        """
        Ensure the __str__ method returns the correct string.
        """
        self.assertEqual(str(self.product1), "Test Product 1 ($19.99)")

    def test_is_expensive_method(self):
        """
        Ensure the custom is_expensive method works correctly.
        """
        self.assertFalse(self.product1.is_expensive())
        self.assertTrue(self.product2.is_expensive())

    def test_unique_name_constraint(self):
        """
        Ensure product names are unique.
        """
        with self.assertRaises(Exception): # Expecting an IntegrityError or similar
            Product.objects.create(
                name="Test Product 1", # Duplicate name
                price=5.00
            )

# --- Serializer Tests ---
class ProductSerializerTest(TestCase):
    """
    Tests for the ProductSerializer.
    """
    def setUp(self):
        self.product_data = {
            'name': 'New Product',
            'description': 'A fantastic new item.',
            'price': 25.00,
            'is_available': True,
        }
        self.product = Product.objects.create(**self.product_data)
        self.product_serializer = ProductSerializer(instance=self.product)

    def test_serializer_contains_expected_fields(self):
        """
        Ensure the serializer output contains all expected fields.
        """
        data = self.product_serializer.data
        self.assertCountEqual(data.keys(), [
            'id', 'name', 'description', 'price',
            'is_available', 'created_at', 'updated_at',
            'is_expensive_product'
        ])

    def test_serializer_field_content(self):
        """
        Ensure the serializer output for fields matches the model instance.
        """
        data = self.product_serializer.data
        self.assertEqual(data['name'], self.product.name)
        self.assertEqual(data['description'], self.product.description)
        self.assertEqual(float(data['price']), float(self.product.price)) # Compare floats
        self.assertEqual(data['is_available'], self.product.is_available)
        # Check custom field
        self.assertEqual(data['is_expensive_product'], self.product.is_expensive())

    def test_serializer_validation_for_negative_price(self):
        """
        Ensure serializer rejects negative prices.
        """
        invalid_data = self.product_data.copy()
        invalid_data['price'] = -10.00
        serializer = ProductSerializer(data=invalid_data)
        self.assertFalse(serializer.is_valid())
        self.assertIn('price', serializer.errors)
        self.assertEqual(
            serializer.errors['price'][0], "Price cannot be negative."
        )

    def test_serializer_valid_data(self):
        """
        Ensure serializer accepts valid data and creates an instance.
        """
        new_product_data = {
            'name': 'Another Product',
            'description': 'Just another one.',
            'price': 50.00,
            'is_available': False,
        }
        serializer = ProductSerializer(data=new_product_data)
        self.assertTrue(serializer.is_valid(), serializer.errors) # Print errors if not valid
        product = serializer.save()
        self.assertEqual(product.name, 'Another Product')
        self.assertEqual(Product.objects.count(), 2)

    def test_serializer_read_only_fields(self):
        """
        Ensure read-only fields are not modifiable through the serializer.
        """
        updated_data = {
            'name': 'Updated Product Name',
            'created_at': '2000-01-01T00:00:00Z', # Attempt to change read-only field
            'updated_at': '2000-01-01T00:00:00Z', # Attempt to change read-only field
        }
        serializer = ProductSerializer(instance=self.product, data=updated_data, partial=True)
        self.assertTrue(serializer.is_valid(), serializer.errors)
        product = serializer.save()

        # The timestamps should not have changed to the provided values
        self.assertNotEqual(product.created_at.isoformat(), '2000-01-01T00:00:00Z')
        self.assertNotEqual(product.updated_at.isoformat(), '2000-01-01T00:00:00Z')
        # Only the 'name' should have been updated
        self.assertEqual(product.name, 'Updated Product Name')


# --- ViewSet Tests ---
class ProductViewSetTest(APITestCase):
    """
    Tests for the ProductViewSet.
    Uses APITestCase to make API requests.
    """
    def setUp(self):
        """
        Set up data and client for API tests.
        """
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.admin_user = User.objects.create_superuser(username='admin', password='adminpassword')

        # Products for testing
        self.product1 = Product.objects.create(
            name="Test Product A", description="Desc A", price=10.00, is_available=True
        )
        self.product2 = Product.objects.create(
            name="Test Product B", description="Desc B", price=200.00, is_available=False
        )
        self.product_list_url = reverse('product-list')
        self.product_detail_url = reverse('product-detail', args=[self.product1.id])
        self.product1_set_availability_url = reverse('product-set-availability', args=[self.product1.id])
        self.available_products_url = reverse('product-available')


    def test_list_products(self):
        """
        Ensure we can retrieve a list of products.
        """
        response = self.client.get(self.product_list_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['results']), 2) # Check against 'results' for paginated output

    def test_create_product_authenticated(self):
        """
        Ensure an authenticated user can create a product.
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': 'New Gadget', 'description': 'Cool item', 'price': 99.99, 'is_available': True}
        response = self.client.post(self.product_list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Product.objects.count(), 3)
        self.assertEqual(Product.objects.get(name='New Gadget').price, 99.99)

    def test_create_product_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot create a product (due to IsAuthenticatedOrReadOnly).
        """
        data = {'name': 'Forbidden Gadget', 'description': 'Forbidden item', 'price': 10.00, 'is_available': True}
        response = self.client.post(self.product_list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertEqual(Product.objects.count(), 2) # No new product created

    def test_retrieve_product(self):
        """
        Ensure we can retrieve a single product.
        """
        response = self.client.get(self.product_detail_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['name'], self.product1.name)

    def test_update_product_authenticated(self):
        """
        Ensure an authenticated user can update a product.
        """
        self.client.force_authenticate(user=self.user)
        updated_data = {'name': 'Updated Product A', 'price': 15.00}
        response = self.client.patch(self.product_detail_url, updated_data, format='json') # Use PATCH for partial update
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.product1.refresh_from_db()
        self.assertEqual(self.product1.name, 'Updated Product A')
        self.assertEqual(float(self.product1.price), 15.00)

    def test_update_product_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot update a product.
        """
        updated_data = {'name': 'Unauthorized Update', 'price': 15.00}
        response = self.client.patch(self.product_detail_url, updated_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.product1.refresh_from_db()
        self.assertNotEqual(self.product1.name, 'Unauthorized Update')

    def test_delete_product_authenticated(self):
        """
        Ensure an authenticated user can delete a product.
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.delete(self.product_detail_url)
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertEqual(Product.objects.count(), 1)
        self.assertFalse(Product.objects.filter(id=self.product1.id).exists())

    def test_delete_product_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot delete a product.
        """
        response = self.client.delete(self.product_detail_url)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertEqual(Product.objects.count(), 2) # Product should still exist

    def test_set_availability_action(self):
        """
        Ensure the custom 'set_availability' action works.
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.post(self.product1_set_availability_url, {'is_available': False}, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.product1.refresh_from_db()
        self.assertFalse(self.product1.is_available)
        self.assertEqual(response.data['id'], self.product1.id)

    def test_set_availability_action_invalid_data(self):
        """
        Ensure the 'set_availability' action handles invalid data.
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.post(self.product1_set_availability_url, {'is_available': 'not_a_bool'}, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("Please provide a boolean value for 'is_available'.", response.data['detail'])

    def test_available_products_action(self):
        """
        Ensure the custom 'available' action lists only available products.
        """
        response = self.client.get(self.available_products_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        # Only product1 is available in setUp
        self.assertEqual(len(response.data['results']), 1)
        self.assertEqual(response.data['results'][0]['name'], self.product1.name)

        # Make product2 available and re-test
        self.product2.is_available = True
        self.product2.save()
        response = self.client.get(self.available_products_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['results']), 2)


# --- Initial setup to run tests ---
# To run these tests:
# 1. Make sure you have Django and DRF installed.
# 2. Set up your Django project and app as described above.
# 3. Run `python manage.py makemigrations`
# 4. Run `python manage.py migrate`
# 5. Run `python manage.py test myapp`

from django.test import TestCase
from myapp.models import Product
from myapp.serializers import ProductSerializer
from decimal import Decimal
import datetime

class ProductSerializerTest(TestCase):
    """
    Tests for the ProductSerializer.
    """
    def setUp(self):
        """
        Set up data for serializer tests.
        """
        self.product_data = {
            "name": "Smartphone",
            "description": "Latest model smartphone with advanced features.",
            "price": Decimal('800.00'),
            "stock": 50
        }
        self.product = Product.objects.create(**self.product_data)
        self.serializer = ProductSerializer(instance=self.product)

    def test_serializer_contains_expected_fields(self):
        """
        Verify that the serializer output contains all expected fields.
        """
        data = self.serializer.data
        self.assertCountEqual(data.keys(), ['id', 'name', 'description', 'price', 'stock', 'is_available', 'created_at', 'updated_at'])

    def test_serializer_data(self):
        """
        Verify that the serialized data matches the model instance.
        """
        data = self.serializer.data
        self.assertEqual(data['name'], self.product_data['name'])
        self.assertEqual(Decimal(data['price']), self.product_data['price']) # Convert to Decimal for exact comparison
        self.assertEqual(data['stock'], self.product_data['stock'])
        self.assertTrue(data['is_available']) # Stock is 50, so should be available

    def test_serializer_create(self):
        """
        Test creating a new product via the serializer.
        """
        new_product_data = {
            "name": "Headphones",
            "description": "Noise-cancelling headphones.",
            "price": Decimal('150.00'),
            "stock": 20
        }
        serializer = ProductSerializer(data=new_product_data)
        self.assertTrue(serializer.is_valid())
        product = serializer.save()

        self.assertEqual(product.name, new_product_data['name'])
        self.assertEqual(product.stock, new_product_data['stock'])
        self.assertTrue(product.id)

    def test_serializer_update(self):
        """
        Test updating an existing product via the serializer.
        """
        update_data = {
            "name": "Updated Smartphone Name",
            "price": Decimal('750.00'),
            "stock": 45
        }
        serializer = ProductSerializer(instance=self.product, data=update_data, partial=True)
        self.assertTrue(serializer.is_valid())
        updated_product = serializer.save()

        self.assertEqual(updated_product.name, update_data['name'])
        self.assertEqual(updated_product.price, update_data['price'])
        self.assertEqual(updated_product.stock, update_data['stock'])
        self.assertNotEqual(updated_product.updated_at, self.product.updated_at) # Check if updated_at changed

    def test_serializer_validation_negative_price(self):
        """
        Test serializer validation for negative price.
        """
        invalid_data = self.product_data.copy()
        invalid_data['name'] = "Invalid Product Price" # Unique name
        invalid_data['price'] = Decimal('-10.00')
        serializer = ProductSerializer(data=invalid_data)
        self.assertFalse(serializer.is_valid())
        self.assertIn('price', serializer.errors)
        self.assertEqual(str(serializer.errors['price'][0]), "Price cannot be negative.")

    def test_serializer_validation_negative_stock(self):
        """
        Test serializer validation for negative stock.
        """
        invalid_data = self.product_data.copy()
        invalid_data['name'] = "Invalid Product Stock" # Unique name
        invalid_data['stock'] = -5
        serializer = ProductSerializer(data=invalid_data)
        self.assertFalse(serializer.is_valid())
        self.assertIn('stock', serializer.errors)
        self.assertEqual(str(serializer.errors['stock'][0]), "Stock cannot be negative.")

    def test_serializer_read_only_fields(self):
        """
        Test that read-only fields cannot be set by the client.
        """
        read_only_data = self.product_data.copy()
        read_only_data['name'] = "Test Read Only"
        read_only_data['created_at'] = datetime.datetime(2000, 1, 1, tzinfo=datetime.timezone.utc)
        serializer = ProductSerializer(data=read_only_data)
        self.assertTrue(serializer.is_valid())
        product = serializer.save()
        # Ensure created_at is still auto_now_add and not what we tried to set
        self.assertNotEqual(product.created_at, datetime.datetime(2000, 1, 1, tzinfo=datetime.timezone.utc))

        # Test updating a read-only field
        update_read_only_data = {'updated_at': datetime.datetime(2000, 1, 1, tzinfo=datetime.timezone.utc)}
        serializer = ProductSerializer(instance=self.product, data=update_read_only_data, partial=True)
        self.assertTrue(serializer.is_valid())
        updated_product = serializer.save()
        # Ensure updated_at is still auto_now and not what we tried to set
        self.assertNotEqual(updated_product.updated_at, datetime.datetime(2000, 1, 1, tzinfo=datetime.timezone.utc))
        self.assertGreater(updated_product.updated_at, self.product.updated_at) # Should be updated by save

from rest_framework.test import APITestCase
from rest_framework import status
from django.urls import reverse
from django.contrib.auth import get_user_model
from decimal import Decimal
from .models import Item

User = get_user_model()

class ItemAPITestCase(APITestCase):
    """
    Test suite for the Item API endpoints.
    """

    def setUp(self):
        """
        Set up common test data and users.
        """
        # Create a regular user
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        # Create an admin user for admin-only actions
        self.admin_user = User.objects.create_superuser(username='admin', password='adminpassword', email='admin@example.com')

        # Create some test items
        self.item1 = Item.objects.create(name='Laptop', description='Powerful laptop', price=Decimal('1200.00'), is_available=True)
        self.item2 = Item.objects.create(name='Mouse', description='Wireless mouse', price=Decimal('25.50'), is_available=False)

        # URLs for the API
        self.list_url = reverse('item-list') # 'item' is the basename defined in myapp/urls.py
        self.detail_url = reverse('item-detail', args=[self.item1.id])
        self.mark_unavailable_url = reverse('item-mark-unavailable', args=[self.item1.id])
        self.available_items_url = reverse('item-available-items')

    def test_create_item_authenticated(self):
        """
        Ensure an authenticated user can create a new item.
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': 'Keyboard', 'description': 'Mechanical keyboard', 'price': '150.00', 'is_available': True}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Item.objects.count(), 3)
        self.assertEqual(Item.objects.get(name='Keyboard').price, Decimal('150.00'))

    def test_create_item_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot create a new item.
        """
        data = {'name': 'Monitor', 'description': '4K Monitor', 'price': '400.00'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertEqual(Item.objects.count(), 2)

    def test_list_items(self):
        """
        Ensure we can retrieve a list of items (unauthenticated allowed).
        """
        response = self.client.get(self.list_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['results']), 2) # Check 'results' due to pagination
        # Check if ordering is applied (descending by created_at)
        self.assertEqual(response.data['results'][0]['name'], self.item2.name) # Item 2 might have been created last depending on creation speed
        self.assertEqual(response.data['results'][1]['name'], self.item1.name)

    def test_retrieve_item_detail(self):
        """
        Ensure we can retrieve a single item (unauthenticated allowed).
        """
        response = self.client.get(self.detail_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['name'], self.item1.name)
        self.assertEqual(Decimal(response.data['price']), self.item1.price)

    def test_update_item_authenticated(self):
        """
        Ensure an authenticated user can update an item.
        """
        self.client.force_authenticate(user=self.user)
        updated_data = {'name': 'Updated Laptop', 'description': 'Even more powerful', 'price': '1300.00', 'is_available': False}
        response = self.client.put(self.detail_url, updated_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.item1.refresh_from_db()
        self.assertEqual(self.item1.name, 'Updated Laptop')
        self.assertEqual(self.item1.price, Decimal('1300.00'))
        self.assertFalse(self.item1.is_available)

    def test_update_item_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot update an item.
        """
        updated_data = {'name': 'Attempted Update', 'price': '999.99'}
        response = self.client.put(self.detail_url, updated_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.item1.refresh_from_db()
        self.assertNotEqual(self.item1.name, 'Attempted Update')

    def test_partial_update_item_authenticated(self):
        """
        Ensure an authenticated user can partially update an item.
        """
        self.client.force_authenticate(user=self.user)
        partial_data = {'price': '1250.00'}
        response = self.client.patch(self.detail_url, partial_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.item1.refresh_from_db()
        self.assertEqual(self.item1.price, Decimal('1250.00'))
        self.assertEqual(self.item1.name, 'Laptop') # Name should remain unchanged

    def test_delete_item_authenticated(self):
        """
        Ensure an authenticated user can delete an item.
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.delete(self.detail_url)
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertEqual(Item.objects.count(), 1)
        self.assertFalse(Item.objects.filter(id=self.item1.id).exists())

    def test_delete_item_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot delete an item.
        """
        response = self.client.delete(self.detail_url)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertEqual(Item.objects.count(), 2)

    def test_filter_items_by_availability(self):
        """
        Ensure items can be filtered by 'is_available'.
        """
        response = self.client.get(self.list_url + '?is_available=True', format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['results']), 1)
        self.assertEqual(response.data['results'][0]['name'], self.item1.name) # Laptop is available

    def test_search_items_by_name(self):
        """
        Ensure items can be searched by 'name'.
        """
        response = self.client.get(self.list_url + '?search=Mouse', format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['results']), 1)
        self.assertEqual(response.data['results'][0]['name'], self.item2.name)

    def test_ordering_items_by_price(self):
        """
        Ensure items can be ordered by 'price'.
        """
        # Order by price ascending
        response = self.client.get(self.list_url + '?ordering=price', format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['results'][0]['name'], self.item2.name) # Mouse (25.50) first
        self.assertEqual(response.data['results'][1]['name'], self.item1.name) # Laptop (1200.00) second

        # Order by price descending
        response = self.client.get(self.list_url + '?ordering=-price', format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['results'][0]['name'], self.item1.name) # Laptop first
        self.assertEqual(response.data['results'][1]['name'], self.item2.name) # Mouse second

    def test_mark_unavailable_action_admin_user(self):
        """
        Ensure an admin user can mark an item as unavailable.
        """
        self.client.force_authenticate(user=self.admin_user)
        response = self.client.post(self.mark_unavailable_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.item1.refresh_from_db()
        self.assertFalse(self.item1.is_available)
        self.assertEqual(response.data['name'], self.item1.name)

    def test_mark_unavailable_action_regular_user(self):
        """
        Ensure a regular authenticated user cannot mark an item as unavailable.
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.post(self.mark_unavailable_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN) # Forbidden for non-admin
        self.item1.refresh_from_db()
        self.assertTrue(self.item1.is_available) # Should remain available

    def test_mark_unavailable_action_unauthenticated(self):
        """
        Ensure an unauthenticated user cannot mark an item as unavailable.
        """
        response = self.client.post(self.mark_unavailable_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.item1.refresh_from_db()
        self.assertTrue(self.item1.is_available)

    def test_available_items_action(self):
        """
        Ensure the 'available_items' custom action works.
        """
        response = self.client.get(self.available_items_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['name'], self.item1.name)
        self.assertTrue(response.data[0]['is_available'])

        # Mark item1 unavailable to check again
        self.item1.is_available = False
        self.item1.save()
        response = self.client.get(self.available_items_url, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 0) # Now no items are available

    def test_create_item_with_invalid_price(self):
        """
        Ensure item creation fails with a negative price.
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': 'Invalid Item', 'description': 'Test negative price', 'price': '-10.00'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("Price cannot be negative.", response.data['price'][0])
        self.assertEqual(Item.objects.count(), 2) # No new item created

    def test_create_item_with_empty_name(self):
        """
        Ensure item creation fails with an empty or whitespace name.
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': '   ', 'description': 'Empty name', 'price': '10.00'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("Item name cannot be empty or just whitespace.", response.data['name'][0])
        self.assertEqual(Item.objects.count(), 2)

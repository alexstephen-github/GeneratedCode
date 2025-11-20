from rest_framework.test import APITestCase, APIClient
from rest_framework import status
from django.urls import reverse
from django.contrib.auth import get_user_model
from myapp.models import Item

User = get_user_model()

class ItemAPITest(APITestCase):
    """
    Test suite for the Item model API endpoints.
    """
    def setUp(self):
        """
        Set up test data and client for all tests.
        """
        self.client = APIClient()
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.admin_user = User.objects.create_superuser(username='adminuser', password='adminpassword')

        self.item1 = Item.objects.create(name='Test Item 1', description='Description for test item 1')
        self.item2 = Item.objects.create(name='Test Item 2', description='Description for test item 2')

        self.list_url = reverse('item-list') # 'item' is the basename defined in myapp/urls.py
        self.detail_url = reverse('item-detail', kwargs={'pk': self.item1.pk})

    # --- Authentication and Permissions Tests ---

    def test_list_items_unauthenticated(self):
        """
        Ensure unauthenticated users can list items (read-only).
        """
        response = self.client.get(self.list_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['results']), 2) # Assuming pagination is enabled

    def test_retrieve_item_unauthenticated(self):
        """
        Ensure unauthenticated users can retrieve a single item (read-only).
        """
        response = self.client.get(self.detail_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['name'], self.item1.name)

    def test_create_item_unauthenticated_fails(self):
        """
        Ensure unauthenticated users cannot create items.
        """
        data = {'name': 'New Item', 'description': 'New Description'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertEqual(Item.objects.count(), 2)

    def test_update_item_unauthenticated_fails(self):
        """
        Ensure unauthenticated users cannot update items.
        """
        data = {'name': 'Updated Item 1'}
        response = self.client.patch(self.detail_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.item1.refresh_from_db()
        self.assertNotEqual(self.item1.name, 'Updated Item 1')

    def test_delete_item_unauthenticated_fails(self):
        """
        Ensure unauthenticated users cannot delete items.
        """
        response = self.client.delete(self.detail_url)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertEqual(Item.objects.count(), 2)

    # --- Authenticated User Tests (Read-Write) ---

    def test_create_item_authenticated(self):
        """
        Ensure authenticated users can create items.
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': 'New Item Auth', 'description': 'New Description Auth'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Item.objects.count(), 3)
        self.assertEqual(Item.objects.get(name='New Item Auth').description, 'New Description Auth')

    def test_update_item_authenticated(self):
        """
        Ensure authenticated users can update items.
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': 'Updated Item Auth'}
        response = self.client.patch(self.detail_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.item1.refresh_from_db()
        self.assertEqual(self.item1.name, 'Updated Item Auth')

    def test_delete_item_authenticated(self):
        """
        Ensure authenticated users can delete items.
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.delete(self.detail_url)
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertEqual(Item.objects.count(), 1)
        self.assertFalse(Item.objects.filter(pk=self.item1.pk).exists())

    # --- Data Integrity & Validation Tests ---

    def test_create_item_with_missing_name_fails(self):
        """
        Ensure creating an item without a name fails.
        """
        self.client.force_authenticate(user=self.user)
        data = {'description': 'Item without name'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn('name', response.data) # Check that 'name' field error is present

    def test_create_item_with_duplicate_name_fails(self):
        """
        Ensure creating an item with a duplicate name fails (name is unique).
        """
        self.client.force_authenticate(user=self.user)
        data = {'name': self.item1.name, 'description': 'Duplicate name item'}
        response = self.client.post(self.list_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn('name', response.data)
        self.assertIn('already exists', str(response.data['name']))

    def test_list_items_pagination(self):
        """
        Ensure list view respects pagination settings.
        (Note: Default PAGE_SIZE is 10, so with 2 items, it won't paginate beyond the first page,
        but we can check the structure.)
        """
        self.client.force_authenticate(user=self.user)
        response = self.client.get(self.list_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('count', response.data)
        self.assertIn('next', response.data)
        self.assertIn('previous', response.data)
        self.assertIn('results', response.data)
        self.assertEqual(response.data['count'], 2)
        self.assertEqual(len(response.data['results']), 2)

    def test_partial_update_item(self):
        """
        Ensure partial update works for an item.
        """
        self.client.force_authenticate(user=self.user)
        data = {'description': 'New description only'}
        response = self.client.patch(self.detail_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.item1.refresh_from_db()
        self.assertEqual(self.item1.name, 'Test Item 1') # Name should not have changed
        self.assertEqual(self.item1.description, 'New description only')

    def test_update_read_only_fields_ignored(self):
        """
        Ensure attempts to update read-only fields are ignored.
        """
        self.client.force_authenticate(user=self.user)
        original_created_at = self.item1.created_at
        data = {
            'name': 'Updated name',
            'created_at': '2000-01-01T00:00:00Z' # Attempt to change read-only field
        }
        response = self.client.patch(self.detail_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.item1.refresh_from_db()
        self.assertEqual(self.item1.name, 'Updated name')
        # Ensure created_at was NOT changed
        self.assertEqual(self.item1.created_at.isoformat(timespec='milliseconds').replace('+00:00', 'Z'), 
                         original_created_at.isoformat(timespec='milliseconds').replace('+00:00', 'Z'))

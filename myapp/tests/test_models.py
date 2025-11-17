from django.test import TestCase
from myapp.models import Product
from decimal import Decimal

class ProductModelTest(TestCase):
    """
    Tests for the Product model.
    """
    def setUp(self):
        """
        Set up non-modified objects for every test method.
        """
        self.product1 = Product.objects.create(
            name="Laptop",
            description="Powerful laptop for coding and gaming.",
            price=Decimal('1200.00'),
            stock=10
        )
        self.product2 = Product.objects.create(
            name="Mouse",
            description="Wireless optical mouse.",
            price=Decimal('25.50'),
            stock=0
        )

    def test_product_creation(self):
        """
        Test that a product can be created and its attributes are correct.
        """
        self.assertEqual(self.product1.name, "Laptop")
        self.assertEqual(self.product1.price, Decimal('1200.00'))
        self.assertEqual(self.product1.stock, 10)
        self.assertTrue(self.product1.created_at)
        self.assertTrue(self.product1.updated_at)

    def test_str_representation(self):
        """
        Test the __str__ method of the Product model.
        """
        self.assertEqual(str(self.product1), "Laptop")

    def test_is_in_stock_method(self):
        """
        Test the is_in_stock method.
        """
        self.assertTrue(self.product1.is_in_stock())
        self.assertFalse(self.product2.is_in_stock())

    def test_decrease_stock_method(self):
        """
        Test decreasing product stock.
        """
        self.product1.decrease_stock(5)
        self.assertEqual(self.product1.stock, 5)
        self.assertFalse(self.product2.decrease_stock(1)) # Cannot decrease stock if 0
        self.assertEqual(self.product2.stock, 0) # Stock remains 0

    def test_increase_stock_method(self):
        """
        Test increasing product stock.
        """
        self.product2.increase_stock(10)
        self.assertEqual(self.product2.stock, 10)

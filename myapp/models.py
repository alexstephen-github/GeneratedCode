from django.db import models

class Product(models.Model):
    """
    Represents a product in an inventory system.
    """
    name = models.CharField(max_length=255, unique=True, help_text="Name of the product.")
    description = models.TextField(blank=True, help_text="Detailed description of the product.")
    price = models.DecimalField(max_digits=10, decimal_places=2, help_text="Price of the product.")
    stock = models.IntegerField(default=0, help_text="Current stock level of the product.")
    created_at = models.DateTimeField(auto_now_add=True, help_text="Timestamp when the product was created.")
    updated_at = models.DateTimeField(auto_now=True, help_text="Timestamp when the product was last updated.")

    class Meta:
        ordering = ['name']
        verbose_name = "Product"
        verbose_name_plural = "Products"

    def __str__(self):
        return self.name

    def is_in_stock(self):
        """Checks if the product has stock."""
        return self.stock > 0

    def decrease_stock(self, quantity):
        """Decreases the stock by a given quantity."""
        if self.stock >= quantity:
            self.stock -= quantity
            self.save()
            return True
        return False

    def increase_stock(self, quantity):
        """Increases the stock by a given quantity."""
        self.stock += quantity
        self.save()
        return True

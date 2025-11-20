from django.db import models

class Product(models.Model):
    """
    Represents a product in the inventory.
    """
    name = models.CharField(max_length=255, unique=True, help_text="Name of the product")
    description = models.TextField(blank=True, null=True, help_text="Detailed description of the product")
    price = models.DecimalField(max_digits=10, decimal_places=2, help_text="Price of the product")
    is_available = models.BooleanField(default=True, help_text="Is the product currently available for purchase?")
    created_at = models.DateTimeField(auto_now_add=True, help_text="Timestamp when the product was created")
    updated_at = models.DateTimeField(auto_now=True, help_text="Timestamp when the product was last updated")

    class Meta:
        ordering = ['name'] # Default ordering for querysets
        verbose_name = "Product"
        verbose_name_plural = "Products"

    def __str__(self):
        """
        String representation of the Product object.
        """
        return f"{self.name} (${self.price})"

    def save(self, *args, **kwargs):
        """
        Custom save method can be added here if needed, e.g., for pre-processing.
        """
        super().save(*args, **kwargs)

    def is_expensive(self):
        """
        Example of a custom model method.
        """
        return self.price > 100

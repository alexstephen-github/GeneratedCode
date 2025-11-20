from django.db import models

class Item(models.Model):
    """
    A simple model to represent an item with basic properties.
    """
    name = models.CharField(max_length=255, unique=True, help_text="Name of the item")
    description = models.TextField(blank=True, null=True, help_text="Detailed description of the item")
    price = models.DecimalField(max_digits=10, decimal_places=2, help_text="Price of the item")
    is_available = models.BooleanField(default=True, help_text="Is the item currently available for purchase?")
    created_at = models.DateTimeField(auto_now_add=True, help_text="Timestamp when the item was created")
    updated_at = models.DateTimeField(auto_now=True, help_text="Timestamp when the item was last updated")

    class Meta:
        ordering = ['name'] # Default ordering for querysets
        verbose_name = "Item"
        verbose_name_plural = "Items"

    def __str__(self):
        return self.name

    def save(self, *args, **kwargs):
        # Example of custom save logic if needed
        super().save(*args, **kwargs)

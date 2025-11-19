from django.db import models

# Defines the Item model, representing a simple item in our system.
class Item(models.Model):
    # CharField for a short text string, max_length is required.
    name = models.CharField(max_length=100)
    # TextField for longer text, blank=True means it's not required in forms.
    description = models.TextField(blank=True)
    # DateTimeField for creation timestamp. auto_now_add=True sets the date
    # automatically when the object is first created and prevents future updates.
    created_at = models.DateTimeField(auto_now_add=True)
    # DateTimeField for last update timestamp. auto_now=True updates the date
    # every time the object is saved.
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        # Specifies the default ordering for querysets returned from this model.
        # '-created_at' means descending order (newest first).
        ordering = ['-created_at']

    def __str__(self):
        # Human-readable representation of the object, useful in admin and debugging.
        return self.name

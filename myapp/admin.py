from django.contrib import admin
from .models import Product

@admin.register(Product)
class ProductAdmin(admin.ModelAdmin):
    """
    Admin configuration for the Product model.
    Displays 'name', 'price', and 'created_at' in the list view.
    Allows searching by 'name' and 'description'.
    """
    list_display = ('name', 'price', 'created_at', 'updated_at')
    search_fields = ('name', 'description')
    list_filter = ('created_at', 'updated_at') # Add filtering options
    readonly_fields = ('created_at', 'updated_at') # Make these fields read-only in the admin

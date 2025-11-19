from django.contrib import admin
from .models import Item

# Register the Item model with the Django administration site.
# The @admin.register decorator is a concise way to register models.
@admin.register(Item)
class ItemAdmin(admin.ModelAdmin):
    # list_display controls which fields are shown in the change list page of the admin.
    list_display = ('name', 'description', 'created_at', 'updated_at')
    # search_fields enables a search box to search these fields.
    search_fields = ('name', 'description')
    # list_filter adds filters to the right sidebar of the change list page.
    list_filter = ('created_at', 'updated_at')

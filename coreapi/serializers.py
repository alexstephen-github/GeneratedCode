from rest_framework import serializers
from .models import Item

# ItemSerializer converts Item model instances to JSON and vice versa.
# ModelSerializer is a shortcut for creating serializers that work with Django models.
class ItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = Item # Specifies the model the serializer is based on.
        # 'fields' lists all the fields from the model that should be included
        # in the serialized representation. 'id' is typically included.
        fields = ['id', 'name', 'description', 'created_at', 'updated_at']
        # 'read_only_fields' are included in the API output but cannot be
        # modified via API input. 'created_at' and 'updated_at' are automatically
        # managed by Django, so they are set as read-only.
        read_only_fields = ['created_at', 'updated_at']

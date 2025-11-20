from rest_framework import serializers
from .models import Item

class ItemSerializer(serializers.ModelSerializer):
    """
    Serializer for the Item model.
    Converts Item model instances to JSON representations and vice-versa.
    """
    # You can add custom fields here if they don't directly map to model fields
    # For example, a read-only field:
    # item_status = serializers.SerializerMethodField()

    class Meta:
        model = Item
        fields = ['id', 'name', 'description', 'price', 'is_available', 'created_at', 'updated_at']
        # read_only_fields = ['created_at', 'updated_at'] # Optional: make these fields read-only on updates

    # Example of a SerializerMethodField implementation
    # def get_item_status(self, obj):
    #     if obj.is_available:
    #         return "In Stock"
    #     return "Out of Stock"

    # Custom validation example (for the whole serializer)
    def validate(self, data):
        """
        Check that the price is not negative.
        """
        if data.get('price') is not None and data['price'] < 0:
            raise serializers.ValidationError({"price": "Price cannot be negative."})
        return data

    # Custom validation example (for a single field)
    def validate_name(self, value):
        """
        Check that the name is not just whitespace.
        """
        if not value.strip():
            raise serializers.ValidationError("Item name cannot be empty or just whitespace.")
        return value

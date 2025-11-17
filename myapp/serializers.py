from rest_framework import serializers
from .models import Product

class ProductSerializer(serializers.ModelSerializer):
    """
    Serializer for the Product model.
    Handles converting Product model instances to JSON and vice-versa.
    """
    # Optional: Add custom fields or modify existing ones
    # For example, a read-only field 'is_available' based on stock
    is_available = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = Product
        fields = ['id', 'name', 'description', 'price', 'stock', 'is_available', 'created_at', 'updated_at']
        read_only_fields = ['created_at', 'updated_at'] # These fields are automatically set by Django

    def get_is_available(self, obj):
        """
        Custom method to determine if a product is available (has stock).
        """
        return obj.is_in_stock()

    def validate_price(self, value):
        """
        Custom validation for the price field.
        Ensures the price is not negative.
        """
        if value < 0:
            raise serializers.ValidationError("Price cannot be negative.")
        return value

    def validate_stock(self, value):
        """
        Custom validation for the stock field.
        Ensures the stock is not negative.
        """
        if value < 0:
            raise serializers.ValidationError("Stock cannot be negative.")
        return value

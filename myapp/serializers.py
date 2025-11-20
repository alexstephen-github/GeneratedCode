from rest_framework import serializers
from .models import Product

class ProductSerializer(serializers.ModelSerializer):
    """
    Serializer for the Product model.
    Converts Product model instances to JSON representations and vice-versa.
    """
    # Custom field example: Add a read-only field that indicates if the product is expensive
    is_expensive_product = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = Product
        fields = [
            'id', 'name', 'description', 'price',
            'is_available', 'created_at', 'updated_at',
            'is_expensive_product'
        ]
        read_only_fields = ['created_at', 'updated_at'] # These fields are set by the model, not by user input

    def get_is_expensive_product(self, obj):
        """
        Returns True if the product's price is greater than 100, False otherwise.
        This method is called for the 'is_expensive_product' SerializerMethodField.
        """
        return obj.is_expensive() # Leverage the model method

    def validate_price(self, value):
        """
        Custom validation for the price field.
        Ensures the price is not negative.
        """
        if value < 0:
            raise serializers.ValidationError("Price cannot be negative.")
        return value

    def create(self, validated_data):
        """
        Override create if you need custom logic for saving new instances.
        """
        return super().create(validated_data)

    def update(self, instance, validated_data):
        """
        Override update if you need custom logic for updating existing instances.
        """
        return super().update(instance, validated_data)

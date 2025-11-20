from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.decorators import action
from .models import Product
from .serializers import ProductSerializer

class ProductViewSet(viewsets.ModelViewSet):
    """
    A ViewSet for viewing and editing Product instances.
    Provides standard CRUD operations: list, create, retrieve, update, partial_update, destroy.
    """
    queryset = Product.objects.all().order_by('name') # All products, ordered by name
    serializer_class = ProductSerializer

    # You can add custom actions to your ViewSet
    @action(detail=True, methods=['post'], name='Set Availability')
    def set_availability(self, request, pk=None):
        """
        Custom action to set the availability status of a product.
        Usage: POST /api/products/{id}/set_availability/
        Body: {"is_available": true/false}
        """
        product = self.get_object()
        is_available = request.data.get('is_available')

        if is_available is None or not isinstance(is_available, bool):
            return Response(
                {"detail": "Please provide a boolean value for 'is_available'."},
                status=status.HTTP_400_BAD_REQUEST
            )

        product.is_available = is_available
        product.save()
        serializer = self.get_serializer(product)
        return Response(serializer.data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['get'], name='Available Products')
    def available(self, request):
        """
        Custom action to list only available products.
        Usage: GET /api/products/available/
        """
        available_products = self.get_queryset().filter(is_available=True)
        page = self.paginate_queryset(available_products)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(available_products, many=True)
        return Response(serializer.data)

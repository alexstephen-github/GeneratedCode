from rest_framework import viewsets
from rest_framework.permissions import AllowAny, IsAuthenticatedOrReadOnly
from .models import Product
from .serializers import ProductSerializer

class ProductViewSet(viewsets.ModelViewSet):
    """
    A ViewSet for viewing and editing product instances.
    Provides CRUD operations (Create, Retrieve, Update, Delete) for Products.
    """
    queryset = Product.objects.all().order_by('name')
    serializer_class = ProductSerializer
    permission_classes = [AllowAny] # Or use IsAuthenticatedOrReadOnly, etc.

    # Optional: Override methods for custom logic if needed
    # def perform_create(self, serializer):
    #     serializer.save(created_by=self.request.user) # Example: save creator
    #
    # def perform_update(self, serializer):
    #     serializer.save(updated_by=self.request.user) # Example: save updater

    # Optional: Custom action (e.g., to increase/decrease stock)
    # from rest_framework.decorators import action
    # from rest_framework.response import Response
    # from rest_framework import status
    #
    # @action(detail=True, methods=['post'], url_path='adjust-stock')
    # def adjust_stock(self, request, pk=None):
    #     product = self.get_object()
    #     quantity = request.data.get('quantity', 0)
    #     action_type = request.data.get('action', 'increase') # 'increase' or 'decrease'
    #
    #     try:
    #         quantity = int(quantity)
    #         if quantity < 0:
    #             return Response({"detail": "Quantity cannot be negative."}, status=status.HTTP_400_BAD_REQUEST)
    #     except ValueError:
    #         return Response({"detail": "Quantity must be an integer."}, status=status.HTTP_400_BAD_REQUEST)
    #
    #     if action_type == 'increase':
    #         product.increase_stock(quantity)
    #         message = f"Stock increased by {quantity}."
    #     elif action_type == 'decrease':
    #         if not product.decrease_stock(quantity):
    #             return Response({"detail": "Not enough stock to decrease by this quantity."}, status=status.HTTP_400_BAD_REQUEST)
    #         message = f"Stock decreased by {quantity}."
    #     else:
    #         return Response({"detail": "Invalid action type. Must be 'increase' or 'decrease'."}, status=status.HTTP_400_BAD_REQUEST)
    #
    #     serializer = self.get_serializer(product)
    #     return Response({'status': message, 'product': serializer.data}, status=status.HTTP_200_OK)

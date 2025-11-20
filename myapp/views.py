from rest_framework import viewsets, permissions, filters
from rest_framework.decorators import action
from rest_framework.response import Response
from django_filters.rest_framework import DjangoFilterBackend
from .models import Item
from .serializers import ItemSerializer

class ItemViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows items to be viewed or edited.
    Provides CRUD operations for the Item model.
    """
    queryset = Item.objects.all().order_by('-created_at')
    serializer_class = ItemSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly] # Only authenticated users can create/update/delete

    # Optional: Add filtering capabilities
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['is_available', 'price'] # Fields to allow exact match filtering
    search_fields = ['name', 'description']      # Fields to allow searching (case-insensitive)
    ordering_fields = ['name', 'price', 'created_at'] # Fields to allow ordering

    # Custom action example: Mark an item as unavailable
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAdminUser])
    def mark_unavailable(self, request, pk=None):
        item = self.get_object()
        item.is_available = False
        item.save()
        serializer = self.get_serializer(item)
        return Response(serializer.data)

    # Custom action example: List available items (on the list route)
    @action(detail=False, methods=['get'])
    def available_items(self, request):
        available = Item.objects.filter(is_available=True)
        serializer = self.get_serializer(available, many=True)
        return Response(serializer.data)

    # You can override methods like `perform_create`, `perform_update`, `perform_destroy`
    # def perform_create(self, serializer):
    #     # Example: Set owner of the item to the requesting user
    #     serializer.save(owner=self.request.user)

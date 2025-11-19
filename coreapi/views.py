from rest_framework import viewsets
from .models import Item
from .serializers import ItemSerializer
from rest_framework.permissions import IsAuthenticated, IsAuthenticatedOrReadOnly

# ItemViewSet provides the API endpoints for our Item model.
# ModelViewSet automatically provides 'list', 'create', 'retrieve', 'update',
# 'partial_update', and 'destroy' actions, greatly simplifying API development.
class ItemViewSet(viewsets.ModelViewSet):
    """
    A ViewSet for viewing and editing Item instances.
    Provides standard RESTful operations: `list`, `create`, `retrieve`,
    `update`, `partial_update`, and `destroy`.
    """
    # The queryset that should be used for retrieving objects from this viewset.
    # In this case, all Item objects.
    queryset = Item.objects.all()
    # The serializer class that should be used for validating and deserializing
    # input, and for serializing output.
    serializer_class = ItemSerializer
    # Permission classes define who can access these views.
    # [IsAuthenticated] means only authenticated users can perform any action.
    # [IsAuthenticatedOrReadOnly] means authenticated users have full access,
    # while unauthenticated users can only perform read actions (GET, HEAD, OPTIONS).
    permission_classes = [IsAuthenticated]
    # permission_classes = [IsAuthenticatedOrReadOnly] # Uncomment this for public read-only access

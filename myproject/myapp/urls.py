from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ItemViewSet

# Create a router and register our ViewSets with it.
router = DefaultRouter()
router.register(r'items', ItemViewSet, basename='item') # The basename argument is used to specify the base name for the URL patterns that are created by the router. This is useful for reverse lookups.

# The API URLs are now determined automatically by the router.
urlpatterns = [
    path('', include(router.urls)),
]

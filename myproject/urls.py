"""
URL configuration for myproject project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include

# This is the project-level URL configuration.
urlpatterns = [
    path('admin/', admin.site.urls), # Django Admin interface.
    path('api/', include('coreapi.urls')), # Includes all URLs defined within our 'coreapi' app.
    # 'api-auth/' provides login/logout views for the browsable API and allows
    # clients to authenticate using HTTP Basic or Session authentication.
    path('api-auth/', include('rest_framework.urls')),
]

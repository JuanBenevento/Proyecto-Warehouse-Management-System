import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const requiredRole = route.data['role'];

  if (!requiredRole || authService.hasRole(requiredRole)) {
    return true;
  }

  alert('⛔ Acceso Denegado: Se requiere rol ' + requiredRole);
  router.navigate(['/dashboard']);
  return false;
};
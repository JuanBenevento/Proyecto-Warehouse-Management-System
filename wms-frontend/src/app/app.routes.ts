import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';


export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard], 
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { 
        path: 'dashboard', 
        loadComponent: () => import('./features/warehouse/warehouse-map/warehouse-map')
          .then(m => m.WarehouseMapComponent) 
      },
      { 
        path: 'ingresos', 
        loadComponent: () => import('./features/inventory/inventory-receive/inventory-receive')
          .then(m => m.InventoryReceiveComponent) 
      },
      { 
        path: 'ConfirmarUbicacion', 
        loadComponent: () => import('./features/inventory/stock-movement/stock-movement')
          .then(m => m.StockMovement) 
      },
      { 
        path: 'inventario', 
        loadComponent: () => import('./features/inventory/inventory-list/inventory-list')
          .then(m => m.InventoryListComponent) 
      },
      { 
        path: 'estrategia', 
        loadComponent: () => import('./features/inventory/put-away-strategy/put-away-strategy')
          .then(m => m.PutAwayStrategyComponent) 
      },
      { 
        path: 'salidas', 
        loadComponent: () => import('./features/inventory/outbound/outbound')
          .then(m => m.OutboundComponent) 
      },
      { 
        path: 'productos', 
        loadComponent: () => import('./features/inventory/product-list/product-list')
          .then(m => m.ProductListComponent) 
      },
      { 
        path: 'ubicaciones', 
        loadComponent: () => import('./features/warehouse/location-create/location-create')
          .then(m => m.LocationCreateComponent),
        canActivate: [roleGuard], 
        data: { role: 'ADMIN' }
      },
      { 
        path: 'nuevo-producto', 
        loadComponent: () => import('./features/inventory/product-create/product-create')
          .then(m => m.ProductCreateComponent),
        canActivate: [roleGuard], 
        data: { role: 'ADMIN' } 
      },
      { 
        path: 'usuarios', 
        loadComponent: () => import('./features/admin/user-management/user-management')
          .then(m => m.UserManagementComponent), 
        canActivate: [roleGuard], 
        data: { role: 'ADMIN' } 
      }
    ]
  },
  { path: '**', redirectTo: '/dashboard' } 
];
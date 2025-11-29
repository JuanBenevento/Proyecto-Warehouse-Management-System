import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { ProductCreate } from "./features/inventory/product-create/product-create";
import { ProductList } from './features/inventory/product-list/product-list';
import { InventoryReceiveComponent } from "./features/inventory/inventory-receive/inventory-receive";
import { AuthService } from './core/services/auth.service';
import { LoginComponent } from './features/auth/login/login';

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, RouterOutlet, ProductCreate, ProductList, InventoryReceiveComponent, LoginComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'wms-frontend';
  authService = inject(AuthService); 
 
  isLoggedIn() {
    return this.authService.isAuthenticated();
  }

  logout() {
    this.authService.logout();
  }
}

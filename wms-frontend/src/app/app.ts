import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar';
import { LoginComponent } from './features/auth/login/login';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, LoginComponent],
  templateUrl: './app.html'
})
export class AppComponent {
  authService = inject(AuthService);

  isLoggedIn() {
    return this.authService.isAuthenticated();
  }
}
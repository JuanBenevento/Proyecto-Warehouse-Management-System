import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.html'
})
export class HeaderComponent {
  authService = inject(AuthService);
  private router = inject(Router);
  
  today = new Date();

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
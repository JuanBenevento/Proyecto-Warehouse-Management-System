import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html'
})
export class LoginComponent {
  authService = inject(AuthService);
  
  creds = { username: '', password: '' };
  errorMsg = '';

  onLogin() {
    this.authService.login(this.creds).subscribe({
      next: () => {
        window.location.reload(); 
      },
      error: () => {
        this.errorMsg = 'Credenciales inválidas';
      }
    });
  }
}
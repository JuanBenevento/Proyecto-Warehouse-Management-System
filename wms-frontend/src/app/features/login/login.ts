import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router'; 
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html', 
  styleUrl: './login.css'
})
export class LoginComponent {
  private authService = inject(AuthService); // Private es buena práctica si no lo usas en el HTML
  private router = inject(Router); 
  
  creds = { username: '', password: '' };
  errorMsg = '';
  isLoading = false; // Para deshabilitar el botón mientras carga

  onLogin() {
    this.isLoading = true;
    this.errorMsg = '';

    this.authService.login(this.creds).subscribe({
      next: () => {
        if (this.authService.hasRole('SUPER_ADMIN')) {
          this.router.navigate(['/saas-panel']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.errorMsg = 'Credenciales inválidas o usuario inactivo.';
        this.isLoading = false;
      }
    });
  }
}
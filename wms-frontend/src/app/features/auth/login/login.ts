import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router'; 
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html', 
  styleUrl: './login.css'
})
export class LoginComponent {
  authService = inject(AuthService);
  router = inject(Router); 
  
  creds = { username: '', password: '' };
  errorMsg = '';

  onLogin() {
    this.authService.login(this.creds).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']); 
      },
      error: () => {
        this.errorMsg = 'Credenciales inválidas';
      }
    });
  }
}
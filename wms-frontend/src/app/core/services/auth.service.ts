import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private tokenKey = 'wms_token'; // Nombre para guardar en el navegador

  // Login: Envía usuario/pass y guarda el token si es correcto
  login(credentials: any) {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
        }
      })
    );
  }

  // Logout: Borra el token
  logout() {
    localStorage.removeItem(this.tokenKey);
    window.location.reload(); // Recarga la página para limpiar estados
  }

  // Obtener el token guardado
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  // Saber si está logueado
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode'; 
import { Router } from '@angular/router';

interface CustomTokenPayload {
  sub: string; 
  role: string; 
  exp: number; 
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private tokenKey = 'wms_token';

  currentUser = signal<string | null>(null);
  currentRole = signal<string | null>(null);

  constructor() {
    if (this.isAuthenticated()) {
      this.decodeToken();
    }
  }

  login(credentials: any) {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
          this.decodeToken(); 
        }
      })
    );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.currentUser.set(null);
    this.currentRole.set(null);
    this.router.navigate(['/login']);
  }

  private decodeToken() {
    const token = this.getToken();
    if (token) {
      try {
        const decoded = jwtDecode<CustomTokenPayload>(token);
        this.currentUser.set(decoded.sub); 
        this.currentRole.set(decoded.role);
      } catch (error) {
        console.error('Token inválido', error);
        this.logout();
      }
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    const decoded = jwtDecode<CustomTokenPayload>(token);
    const isExpired = decoded.exp * 1000 < Date.now();
    
    if (isExpired) {
      this.logout();
      return false;
    }
    return true;
  }

  hasRole(requiredRole: string): boolean {
    return this.currentRole() === requiredRole;
  }
}
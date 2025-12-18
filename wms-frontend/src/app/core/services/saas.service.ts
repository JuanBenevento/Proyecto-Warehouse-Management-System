import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tenant } from '../models/tenant.model';

@Injectable({ providedIn: 'root' })
export class SaaSService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/saas';

  getTenants(): Observable<Tenant[]> {
    return this.http.get<Tenant[]>(`${this.apiUrl}/tenants`);
  }

  onboardCompany(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/onboarding`, data, { responseType: 'text' });
  }

  updateStatus(id: string, status: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/status`, {}, { 
      params: { status } 
    });
  }

  deleteTenant(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
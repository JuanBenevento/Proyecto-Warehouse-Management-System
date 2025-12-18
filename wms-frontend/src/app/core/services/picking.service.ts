import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PickingService {
  
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1';

  getPutAwaySuggestion(sku: string, quantity: number): Observable<string> {
    const params = new HttpParams()
      .set('sku', sku)
      .set('quantity', quantity);

    return this.http.get(`${this.apiUrl}/inventory/suggest-location`, { 
      params, 
      responseType: 'text' 
    });
  }

  allocateOrder(sku: string, quantity: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/picking/allocate`, { sku, quantity }, { responseType: 'text' });
  }

  shipOrder(sku: string, quantity: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/picking/ship`, { sku, quantity }, { responseType: 'text' });
  }
}
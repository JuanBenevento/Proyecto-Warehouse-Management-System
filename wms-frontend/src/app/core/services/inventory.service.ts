import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InventoryItem } from '../models/inventaryItem.model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/inventory';

  getAllInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(this.apiUrl);
  }

  getPutAwaySuggestion(sku: string, quantity: number): Observable<string> {
    const params = new HttpParams().set('sku', sku).set('quantity', quantity);
    return this.http.get(`${this.apiUrl}/suggest-location`, { params, responseType: 'text' });
  }

  receiveInventory(data: any): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(`${this.apiUrl}/receive`, data);
  }

  confirmPutAway(lpn: string, targetLocationCode: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/put-away`, { lpn, targetLocationCode });
  }

  internalMove(data: { lpn: string, targetLocationCode: string, reason: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/move`, data);
  }

  adjustInventory(data: { lpn: string, newQuantity: number, reason: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/adjust`, data);
  }
}
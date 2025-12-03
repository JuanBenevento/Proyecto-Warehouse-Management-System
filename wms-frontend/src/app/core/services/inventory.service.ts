import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InventoryItemResponse } from '../models/inventaryItemResponse.model';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/inventory';

  receiveInventory(data: any): Observable<InventoryItemResponse> {
    return this.http.post<InventoryItemResponse>(`${this.apiUrl}/receive`, data);
  }

  confirmPutAway(lpn: string, targetLocationCode: string): Observable<void> {
    const body = { lpn, targetLocationCode };
    return this.http.put<void>(`${this.apiUrl}/put-away`, body);
  }

  getAllInventory(): Observable<InventoryItemResponse[]> {
    return this.http.get<InventoryItemResponse[]>(this.apiUrl);
  }
}
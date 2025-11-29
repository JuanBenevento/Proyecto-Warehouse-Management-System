import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventoryItemResponse {
  lpn: string;
  productSku: string;
  quantity: number;
  status: string;
  locationCode: string;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/inventory';

  receiveInventory(data: any): Observable<InventoryItemResponse> {
    return this.http.post<InventoryItemResponse>(`${this.apiUrl}/receive`, data);
  }
}
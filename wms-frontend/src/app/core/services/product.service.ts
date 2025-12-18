import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  // URL Base limpia
  private apiUrl = 'http://localhost:8080/api/v1/products';

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product); 
  }

  updateProduct(sku: string, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${sku}`, product); 
  }

  deleteProduct(sku: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${sku}`); 
  }
}
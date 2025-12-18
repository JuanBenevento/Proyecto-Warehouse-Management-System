import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/auditLog.model';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class AuditService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/audit';

  getAuditLogs(
    sku?: string,
    lpn?: string,
    startDate?: string,
    endDate?: string,
    page: number = 0,
    size: number = 20
  ): Observable<Page<AuditLog>> {

    // Construimos los parámetros dinámicamente
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'timestamp,desc'); // Ordenar por fecha descendente

    if (sku) params = params.set('sku', sku);
    if (lpn) params = params.set('lpn', lpn);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<Page<AuditLog>>(this.apiUrl, { params });
  }
}

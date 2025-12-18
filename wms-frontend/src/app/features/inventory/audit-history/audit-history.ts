import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService, Page } from '../../../core/services/audit.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import { AuditLog } from '../../../core/models/auditLog.model';

@Component({
  selector: 'app-audit-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-history.html'
})
export class AuditHistoryComponent implements OnInit {
  
  private auditService = inject(AuditService);

  // Datos
  logs: AuditLog[] = [];
  isLoading = false;

  // Filtros
  filters = {
    sku: '',
    lpn: '',
    startDate: '',
    endDate: ''
  };

  // Paginación
  pagination = {
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0
  };

  ngOnInit() {
    this.loadData();
  }

  loadData(page: number = 0) {
    this.isLoading = true;
    this.pagination.page = page;

    this.auditService.getAuditLogs(
      this.filters.sku || undefined,
      this.filters.lpn || undefined,
      this.filters.startDate || undefined,
      this.filters.endDate || undefined,
      this.pagination.page,
      this.pagination.size
    ).subscribe({
      next: (response) => {
        this.logs = response.content;
        this.pagination.totalElements = response.totalElements;
        this.pagination.totalPages = response.totalPages;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        showBackendError(err, 'Error al cargar reporte');
      }
    });
  }

  cleanFilters() {
    this.filters = { sku: '', lpn: '', startDate: '', endDate: '' };
    this.loadData(0);
  }

  // Helpers para colores en la vista
  getTypeColor(type: string): string {
    switch (type) {
      case 'RECEPCION': return 'bg-emerald-100 text-emerald-800 border-emerald-200';
      case 'SALIDA': return 'bg-rose-100 text-rose-800 border-rose-200';
      case 'AJUSTE': return 'bg-amber-100 text-amber-800 border-amber-200';
      case 'MOVIMIENTO': return 'bg-blue-100 text-blue-800 border-blue-200';
      default: return 'bg-slate-100 text-slate-600';
    }
  }
}
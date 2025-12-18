import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SaaSService} from '../../../core/services/saas.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import { Tenant } from '../../../core/models/tenant.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-super-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './super-admin-dashboard.html'
})
export class SuperAdminDashboardComponent implements OnInit {
  
  private saasService = inject(SaaSService);

  tenants: Tenant[] = [];
  showForm = false; 
  
  newData = {
    companyName: '',
    companyId: '',
    adminEmail: '',
    adminUsername: '',
    adminPassword: ''
  };

  ngOnInit() {
    this.loadTenants();
  }

  loadTenants() {
    this.saasService.getTenants().subscribe({
      next: (data) => this.tenants = data,
      error: (err) => showBackendError(err, 'Error cargando clientes')
    });
  }

  createTenant() {
    this.saasService.onboardCompany(this.newData).subscribe({
      next: (msg) => {
        Swal.fire({
          title: '¡Empresa Creada!',
          text: msg,
          icon: 'success',
          confirmButtonColor: '#4f46e5' // Indigo
        });
        this.showForm = false;
        this.resetForm();
        this.loadTenants();
      },
      error: (err) => showBackendError(err, 'Error en Onboarding')
    });
  }

  toggleStatus(t: Tenant) {
    const newStatus = t.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    const actionText = newStatus === 'ACTIVE' ? 'Reactivar' : 'Suspender';
    const color = newStatus === 'ACTIVE' ? '#10b981' : '#f59e0b'; // Verde o Amarillo

    Swal.fire({
      title: `¿${actionText} servicio?`,
      text: `La empresa ${t.name} pasará a estado ${newStatus}.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: `Sí, ${actionText}`,
      confirmButtonColor: color
    }).then((result) => {
      if (result.isConfirmed) {
        this.saasService.updateStatus(t.id, newStatus).subscribe({
          next: () => {
            this.loadTenants();
            const Toast = Swal.mixin({
              toast: true, position: 'top-end', showConfirmButton: false, timer: 3000, timerProgressBar: true
            });
            Toast.fire({ icon: 'success', title: `Empresa ${newStatus}` });
          },
          error: (err) => showBackendError(err, 'Error al cambiar estado')
        });
      }
    });
  }

  deleteTenant(t: Tenant) {
    Swal.fire({
      title: '¿Eliminar Definitivamente?',
      text: `Se eliminará ${t.name} (${t.id}). Esta acción requiere que la empresa no tenga datos.`,
      icon: 'error',
      showCancelButton: true,
      confirmButtonText: 'Sí, Eliminar',
      confirmButtonColor: '#ef4444' // Rojo
    }).then((result) => {
      if (result.isConfirmed) {
        this.saasService.deleteTenant(t.id).subscribe({
          next: () => {
            this.loadTenants();
            Swal.fire('Eliminado', 'La empresa ha sido borrada.', 'success');
          },
          error: (err) => showBackendError(err, 'No se pudo eliminar')
        });
      }
    });
  }

  resetForm() {
    this.newData = { companyName: '', companyId: '', adminEmail: '', adminUsername: '', adminPassword: '' };
  }
}
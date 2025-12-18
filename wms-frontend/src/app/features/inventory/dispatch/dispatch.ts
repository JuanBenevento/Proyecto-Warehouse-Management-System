import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PickingService } from '../../../core/services/picking.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch.html'
})
export class DispatchComponent {
  private pickingService = inject(PickingService);
  private router = inject(Router);

  order = { sku: '', quantity: 1 };
  isProcessing = false;

  onShip() {
    this.isProcessing = true;

    this.pickingService.shipOrder(this.order.sku, this.order.quantity).subscribe({
      next: (msg) => {
        this.isProcessing = false;
        
        Swal.fire({
          title: '¡Despacho Exitoso!',
          text: msg,
          icon: 'success',
          confirmButtonColor: '#e11d48', 
          confirmButtonText: 'Volver a Stock'
        }).then(() => {
          this.router.navigate(['/inventario']); 
        });
        
        this.order = { sku: '', quantity: 1 };
      },
      error: (err) => {
        this.isProcessing = false;
        showBackendError(err, 'Error en Despacho');
      }
    });
  }
}
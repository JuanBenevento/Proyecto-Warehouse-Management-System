import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PickingService } from '../../../core/services/picking.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-outbound',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './outbound.html'
})
export class OutboundComponent {
  private pickingService = inject(PickingService);

  order = { sku: '', quantity: 1 };
  isProcessing = false;
  lastSuccessMessage = '';

  onAllocate() {
    this.isProcessing = true;
    this.lastSuccessMessage = '';

    this.pickingService.allocateOrder(this.order.sku, this.order.quantity).subscribe({
      next: (msg) => {
        this.isProcessing = false;
        this.lastSuccessMessage = msg; // Muestra la tarjeta de éxito
        Swal.fire({
          icon: 'success',
          title: 'Stock Reservado',
          text: msg,
          timer: 2000,
          showConfirmButton: false
        });
        // No limpiamos el form para facilitar ir al despacho con los mismos datos si se quiere
      },
      error: (err) => {
        this.isProcessing = false;
        showBackendError(err, 'Error al Reservar');
      }
    });
  }
}
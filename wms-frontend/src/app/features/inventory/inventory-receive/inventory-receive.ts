import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService} from '../../../core/services/inventory.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import Swal from 'sweetalert2';
import { InventoryItem } from '../../../core/models/inventaryItem.model';

@Component({
  selector: 'app-inventory-receive',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-receive.html'
})
export class InventoryReceiveComponent {
  
  private inventoryService = inject(InventoryService);

  // Modelo del formulario
  receptionData = {
    productSku: '',
    quantity: 1,
    locationCode: '',
    batchNumber: '',
    expiryDate: ''
  };

  // Resultado de la operación
  generatedTicket: InventoryItem | null = null;
  isSubmitting = false;

  onReceive() {
    this.isSubmitting = true;

    this.inventoryService.receiveInventory(this.receptionData).subscribe({
      next: (response: any) => { // 'any' o 'InventoryItem' según tu interfaz
        this.generatedTicket = response;
        this.isSubmitting = false;
        
        // Feedback sonoro/visual rápido
        const Toast = Swal.mixin({
          toast: true, position: 'top-end', showConfirmButton: false, timer: 3000, timerProgressBar: true
        });
        Toast.fire({ icon: 'success', title: 'LPN Generado Exitosamente' });
      },
      error: (err: any) => {
        this.isSubmitting = false;
        showBackendError(err, 'Error en Recepción');
      }
    });
  }

  resetForm() {
    this.generatedTicket = null;
    this.receptionData = {
      productSku: '',
      quantity: 1,
      locationCode: '',
      batchNumber: '',
      expiryDate: ''
    };
  }
}
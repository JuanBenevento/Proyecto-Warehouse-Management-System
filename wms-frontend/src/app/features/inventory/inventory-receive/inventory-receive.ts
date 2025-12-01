import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService, InventoryItemResponse } from '../../../core/services/inventory.service';
import { showBackendError } from '../../../shared/utils/error-handler';

@Component({
  selector: 'app-inventory-receive',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-receive.html',
  styles: []
})
export class InventoryReceiveComponent {
  
  private inventoryService = inject(InventoryService);

  receptionData = {
    productSku: '',
    quantity: 1,
    locationCode: '',     
    batchNumber: '',
    expiryDate: ''
  };

  generatedTicket: InventoryItemResponse | null = null;
  errorMessage: string = '';

  onReceive() {
    this.errorMessage = '';
    this.generatedTicket = null;

    this.inventoryService.receiveInventory(this.receptionData).subscribe({
      next: (response: InventoryItemResponse) => {
        this.generatedTicket = response;
      },
      error: (err: any) => {
        console.error(err);
        showBackendError(err, 'Error en Recepción');
      }
    });
  }
}
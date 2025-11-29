import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService, InventoryItemResponse } from '../../../core/services/inventory.service';

@Component({
  selector: 'app-inventory-receive',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-receive.html',
  styles: []
})
export class InventoryReceiveComponent {
  
  private inventoryService = inject(InventoryService);

  // Modelo del formulario
  receptionData = {
    productSku: '',
    quantity: 1,
    locationCode: '',     // El usuario debe escribir una ubicación válida que hayas creado (ej: A-01-01)
    batchNumber: '',
    expiryDate: ''
  };

  // Aquí guardaremos el "Ticket" generado
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
        this.errorMessage = 'Error: ' + (err.error?.message || 'Verifica los datos');
      }
    });
  }
}
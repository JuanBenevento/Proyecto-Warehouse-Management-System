import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService } from '../../../core/services/inventory.service';

@Component({
  selector: 'app-stock-movement',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-movement.html'
})
export class StockMovement {
  
  private inventoryService = inject(InventoryService);

  data = { lpn: '', locationCode: '' };
  message: string = '';
  isSuccess: boolean = false;

  onConfirm() {
    this.message = 'Procesando...';
    this.inventoryService.confirmPutAway(this.data.lpn, this.data.locationCode)
      .subscribe({
        next: () => {
          this.message = '✅ Stock desbloqueado y disponible para venta.';
          this.isSuccess = true;
          this.data = { lpn: '', locationCode: '' }; 
        },
        error: (err) => {
          this.isSuccess = false;
          this.message = '❌ Error: ' + (err.error?.message || 'No se pudo confirmar');
        }
      });
  }
}
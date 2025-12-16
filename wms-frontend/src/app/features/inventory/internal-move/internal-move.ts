import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService } from '../../../core/services/inventory.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-internal-move',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './internal-move.html'
})
export class InternalMoveComponent {
  
  private inventoryService = inject(InventoryService);

  moveData = {
    lpn: '',
    targetLocationCode: '',
    reason: 'Optimización de Espacio' 
  };

  isSubmitting = false;

  onMove() {
    this.isSubmitting = true;

    this.inventoryService.internalMove(this.moveData).subscribe({
      next: () => {
        this.isSubmitting = false;
        
        const Toast = Swal.mixin({
          toast: true, position: 'top-end', showConfirmButton: false, timer: 2000, timerProgressBar: true
        });
        Toast.fire({ icon: 'success', title: 'Movimiento Registrado' });

        this.moveData.lpn = '';
      },
      error: (err) => {
        this.isSubmitting = false;
        showBackendError(err, 'Error al Mover');
      }
    });
  }
}
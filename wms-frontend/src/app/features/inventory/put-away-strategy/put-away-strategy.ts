import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PickingService } from '../../../core/services/picking.service';
import {showBackendError } from '../../../shared/utils/error-handler'

@Component({
  selector: 'app-put-away-strategy',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './put-away-strategy.html'
})
export class PutAwayStrategyComponent {
  
  private pickingService = inject(PickingService);

  query = { sku: '', quantity: 1 };
  suggestion: string | null = null;

askStrategy() {
    this.pickingService.getPutAwaySuggestion(this.query.sku, this.query.quantity)
      .subscribe({
        next: (locationCode) => {
          this.suggestion = locationCode;
        },
        error: (err) => {
          showBackendError(err, 'Sin Ubicación Disponible');
        }
      });
  }
}
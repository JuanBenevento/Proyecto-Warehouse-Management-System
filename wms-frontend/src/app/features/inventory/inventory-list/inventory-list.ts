import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InventoryService} from '../../../core/services/inventory.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import { InventoryItem } from '../../../core/models/inventaryItem.model';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inventory-list.html'
})
export class InventoryListComponent implements OnInit {
  
  private inventoryService = inject(InventoryService);
  
  inventoryItems: InventoryItem[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.loadInventories();
  }

  loadInventories() {
    this.isLoading = true;
    this.inventoryService.getAllInventory().subscribe({
      next: (data) => {
        this.inventoryItems = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        showBackendError(err, 'Error cargando stock');
      }
    });
  }

  isCritical(status: string): boolean {
    return status === 'DAMAGED' || status === 'EXPIRED';
  }
}
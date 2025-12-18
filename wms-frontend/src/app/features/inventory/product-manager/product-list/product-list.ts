import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { Product } from '../../../../core/models/product.model';
import Swal from 'sweetalert2';
import { showBackendError } from '../../../../shared/utils/error-handler';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.html'
})
export class ProductListComponent implements OnInit {
  
  private productService = inject(ProductService);
  private router = inject(Router);
  
  products: Product[] = [];

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getProducts().subscribe({
      next: (data) => this.products = data,
      error: (err) => showBackendError(err, 'Error cargando catálogo')
    });
  }

  create() {
    this.router.navigate(['/nuevo-producto']);
  }

  edit(p: Product) {
    this.router.navigate(['/nuevo-producto'], { state: { productData: p } });
  }

  delete(sku: string) {
    Swal.fire({
      title: '¿Eliminar Producto?',
      text: `Estás a punto de dar de baja el SKU: ${sku}.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productService.deleteProduct(sku).subscribe({
          next: () => {
            this.loadProducts();
            Swal.fire('Eliminado', 'El producto ha sido dado de baja.', 'success');
          },
          error: (err) => showBackendError(err, 'No se pudo eliminar')
        });
      }
    });
  }
}
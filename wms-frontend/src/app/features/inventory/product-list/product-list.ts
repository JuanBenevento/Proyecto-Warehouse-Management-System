import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core'; 
import { Router } from '@angular/router'; 
import { Product } from '../../../core/models/product.model';
import { ProductService } from '../../../core/services/product.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-product-list',
  standalone: true, 
  imports: [CommonModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
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
      next: (data) => {
        this.products = data;
      },
      error: (err) => console.error(err)
    });
  }

  edit(p: Product) {
    this.router.navigate(['/nuevo-producto'], { 
      state: { productData: p } 
    });
  }

  delete(sku: string) {
    Swal.fire({
      title: '¿Estás seguro?',
      text: `Estás a punto de eliminar el producto ${sku}. No podrás revertir esto.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33', 
      cancelButtonColor: '#3085d6', 
      confirmButtonText: 'Sí, eliminarlo',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.performDelete(sku);
      }
    });
  }

  private performDelete(sku: string) {
    this.productService.deleteProduct(sku).subscribe({
      next: () => {
        // Mostrar éxito visual
        Swal.fire(
          '¡Eliminado!',
          'El producto ha sido dado de baja.',
          'success'
        );
        this.loadProducts();
      },
      error: (err) => {
        Swal.fire(
          'Error',
          'No se pudo eliminar: ' + err.error?.message,
          'error'
        );
      }
    });
  }
}
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../../core/models/product.model';
import { ProductService } from '../../../../core/services/product.service';
import { showBackendError } from '../../../../shared/utils/error-handler'; 
import Swal from 'sweetalert2';

@Component({
  selector: 'app-product-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-create.html',
  styleUrl: './product-create.css',
})
export class ProductCreateComponent implements OnInit {
  
  private productService = inject(ProductService);
  private router = inject(Router); 

  isEditing = false;
  
  product: Product = this.getEmptyProduct();

  ngOnInit() {
    const data = history.state.productData;
    
    if (data) {
      this.isEditing = true;
      this.product = JSON.parse(JSON.stringify(data));
    }
  }

  onSubmit() {
    const payload: any = {
      sku: this.product.sku,
      name: this.product.name,
      description: this.product.description,
      width: this.product.dimensions.width,
      height: this.product.dimensions.height,
      depth: this.product.dimensions.depth,
      weight: this.product.dimensions.weight
    };

    const request = this.isEditing 
      ? this.productService.updateProduct(this.product.sku, payload)
      : this.productService.createProduct(payload);

    request.subscribe({
      next: () => this.showSuccess(
        this.isEditing ? 'Producto actualizado correctamente' : 'Producto creado correctamente'
      ),
      error: (err: any) => showBackendError(err, 'Error de Validación')
    });
  }

  cancelEdit() {
    this.isEditing = false;
    this.product = this.getEmptyProduct();
    this.router.navigate(['/productos']);
  }

  private getEmptyProduct(): Product {
    return {
      sku: '', name: '', description: '',
      dimensions: { width: 0, height: 0, depth: 0, weight: 0 }
    };
  }

  private Toast = Swal.mixin({
    toast: true,
    position: 'top-end', 
    showConfirmButton: false,
    timer: 2000, 
    timerProgressBar: true,
    didOpen: (toast) => {
      toast.addEventListener('mouseenter', Swal.stopTimer)
      toast.addEventListener('mouseleave', Swal.resumeTimer)
    }
  });

  private showSuccess(msg: string) {
    this.Toast.fire({
      icon: 'success',
      title: msg
    });

    if (!this.isEditing) {
        this.product = this.getEmptyProduct(); 
    } else {
        setTimeout(() => this.router.navigate(['/productos']), 1000);
    }
    this.isEditing = false;
  }
}
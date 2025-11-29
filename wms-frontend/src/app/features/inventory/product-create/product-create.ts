import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../core/models/product.model';
import { ProductService } from '../../../core/services/product.service'

@Component({
  selector: 'app-product-create',
  imports: [CommonModule, FormsModule],
  templateUrl: './product-create.html',
  styleUrl: './product-create.css',
})
export class ProductCreate {
  private productService = inject(ProductService);

  // Modelo inicial vacío
  product: Product = {
    sku: '',
    name: '',
    description: '',
    dimensions: { width: 0, height: 0, depth: 0, weight: 0 }
  };

  message: string = '';

  onSubmit() {
    // 1. CREAMOS EL PAYLOAD MANUALMENTE (Mapeo)
    // Transformamos el objeto anidado de Angular al objeto plano de Java
    const payload: any = {
      sku: this.product.sku,
      name: this.product.name,
      description: this.product.description,
      // Sacamos los valores de adentro de 'dimensions'
      width: this.product.dimensions.width,
      height: this.product.dimensions.height,
      depth: this.product.dimensions.depth,
      weight: this.product.dimensions.weight
    };

    // 2. ENVIAMOS EL PAYLOAD TRANSFORMADO
    this.productService.createProduct(payload).subscribe({
      next: (response) => {
        this.message = `¡Producto creado! ID: ${response.id}`;
        // Opcional: Limpiar el formulario
        this.resetForm();
      },
      error: (err) => {
        console.error(err); // Para ver el error real en la consola del navegador
        this.message = 'Error al crear producto. Verifica los datos.';
      }
    });
  }

  // Agrega este helper para limpiar después de guardar
  resetForm() {
    this.product = {
      sku: '',
      name: '',
      description: '',
      dimensions: { width: 0, height: 0, depth: 0, weight: 0 }
    };
  }
}

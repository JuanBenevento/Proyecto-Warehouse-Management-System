package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.CreateProductCommand;
import com.juanbenevento.wms.application.ports.in.ManageProductUseCase;
import com.juanbenevento.wms.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "1. Catálogo de Productos", description = "Gestión de maestros de productos (SKUs, Dimensiones, Peso)")
public class ProductController {

    private final ManageProductUseCase manageProductUseCase;

    public ProductController(ManageProductUseCase manageProductUseCase) {
        this.manageProductUseCase = manageProductUseCase;
    }

    @Operation(summary = "Listar productos", description = "Devuelve todos los productos activos del sistema.")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(manageProductUseCase.getAllProducts());
    }

    @Operation(summary = "Crear producto", description = "Da de alta un nuevo SKU. Requiere validación de dimensiones.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid CreateProductRequest request) {

        CreateProductCommand command = new CreateProductCommand(
                request.sku(), request.name(), request.description(),
                request.width(), request.height(), request.depth(), request.weight()
        );

        Product createdProduct = manageProductUseCase.createProduct(command);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar producto", description = "Modifica datos de un SKU. Si tiene stock físico, no permite cambiar dimensiones.")
    @PutMapping("/{sku}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable String sku, @RequestBody @Valid CreateProductRequest request) {

        CreateProductCommand command = new CreateProductCommand(
                request.sku(), request.name(), request.description(),
                request.width(), request.height(), request.depth(), request.weight()
        );

        return ResponseEntity.ok(manageProductUseCase.updateProduct(sku, command));
    }

    @Operation(summary = "Eliminar producto", description = "Realiza un borrado lógico si no tiene stock asociado.")
    @DeleteMapping("/{sku}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String sku) {
        manageProductUseCase.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }

    public record CreateProductRequest(
            @Schema(example = "TV-LG-65", description = "Código único")
            @NotBlank(message = "El SKU es obligatorio")
            String sku,

            @Schema(example = "Smart TV 65 Pulgadas")
            @NotBlank(message = "El nombre no puede estar vacío")
            String name,

            @Schema(example = "Televisor 4K OLED")
            String description,

            @Schema(example = "145.0") @NotNull @Positive
            Double width,
            @Schema(example = "83.0") @NotNull @Positive
            Double height,
            @Schema(example = "4.5") @NotNull @Positive
            Double depth,
            @Schema(example = "25.0") @NotNull @Positive
            Double weight
    ) {}
}
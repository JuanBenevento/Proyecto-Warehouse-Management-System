package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.CreateProductCommand;
import com.juanbenevento.wms.application.ports.in.CreateProductUseCase;
import com.juanbenevento.wms.application.service.ProductService;
import com.juanbenevento.wms.domain.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @GetMapping("getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(createProductUseCase.getAllProducts());
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid CreateProductRequest request) {

        // 1. Convertir DTO Web (JSON) -> Comando Interno
        CreateProductCommand command = new CreateProductCommand(
                request.sku(),
                request.name(),
                request.description(),
                request.width(),
                request.height(),
                request.depth(),
                request.weight()
        );

        // 2. Ejecutar Caso de Uso
        Product createdProduct = createProductUseCase.createProduct(command);

        // 3. Retornar respuesta
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // DTO interno para recibir el JSON (Record)
    // Lo definimos aquí mismo o en un paquete .dto si prefieres
    public record CreateProductRequest(
            @NotBlank(message = "El SKU es obligatorio")
            String sku,

            @NotBlank(message = "El nombre no puede estar vacío")
            String name,

            String description, // Opcional

            @NotNull(message = "El ancho es obligatorio")
            @Positive(message = "El ancho debe ser mayor a 0")
            Double width,

            @NotNull(message = "El alto es obligatorio")
            @Positive(message = "El alto debe ser mayor a 0")
            Double height,

            @NotNull(message = "La profundidad es obligatoria")
            @Positive(message = "La profundidad debe ser mayor a 0")
            Double depth,

            @NotNull(message = "El peso es obligatorio")
            @Positive(message = "El peso debe ser mayor a 0")
            Double weight
    ) {}
}
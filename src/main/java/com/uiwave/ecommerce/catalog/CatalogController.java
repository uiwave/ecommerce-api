package com.uiwave.ecommerce.catalog;

import com.uiwave.ecommerce.catalog.dto.CategoryResponse;
import com.uiwave.ecommerce.catalog.dto.ProductResponse;
import com.uiwave.ecommerce.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Categorías y productos de la tienda (público)")
public class CatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Operation(summary = "Categorías disponibles")
    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return categoryService.listAll();
    }

    @Operation(summary = "Productos activos con búsqueda, filtro por categoría y paginación")
    @GetMapping("/products")
    public PageResponse<ProductResponse> products(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return productService.list(q, category, page, size);
    }

    @Operation(summary = "Detalle de un producto por slug")
    @GetMapping("/products/{slug}")
    public ProductResponse product(@PathVariable String slug) {
        return productService.getBySlug(slug);
    }
}

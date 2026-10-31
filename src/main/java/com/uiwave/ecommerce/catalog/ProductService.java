package com.uiwave.ecommerce.catalog;

import com.uiwave.ecommerce.catalog.dto.ProductRequest;
import com.uiwave.ecommerce.catalog.dto.ProductResponse;
import com.uiwave.ecommerce.catalog.model.Category;
import com.uiwave.ecommerce.catalog.model.Product;
import com.uiwave.ecommerce.catalog.repository.ProductRepository;
import com.uiwave.ecommerce.category.repository.CategoryRepository;
import com.uiwave.ecommerce.common.dto.PageResponse;
import com.uiwave.ecommerce.common.exception.DuplicateResourceException;
import com.uiwave.ecommerce.common.exception.ResourceNotFoundException;
import com.uiwave.ecommerce.common.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PAGE_SIZE = 48;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ------------------------------------------------------------------ público

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> list(String query, String categorySlug, int page, int size) {
        Pageable pageable = pageable(page, size);
        String cleanQuery = StringUtils.hasText(query) ? query.trim() : null;
        String cleanCategory = StringUtils.hasText(categorySlug) ? categorySlug.trim() : null;

        Page<Product> result;
        if (cleanQuery != null && cleanCategory != null) {
            result = productRepository.searchActiveByCategory(cleanQuery, cleanCategory, pageable);
        } else if (cleanQuery != null) {
            result = productRepository.searchActive(cleanQuery, pageable);
        } else if (cleanCategory != null) {
            result = productRepository.findByActiveTrueAndCategorySlug(cleanCategory, pageable);
        } else {
            result = productRepository.findByActiveTrue(pageable);
        }

        return PageResponse.from(result, ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return ProductResponse.from(product);
    }

    // -------------------------------------------------------------------- admin

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> adminList(
            String query, String categorySlug, Boolean active, int page, int size) {
        String cleanQuery = StringUtils.hasText(query) ? query.trim() : null;
        String cleanCategory = StringUtils.hasText(categorySlug) ? categorySlug.trim() : null;

        Page<Product> result = productRepository.searchAdmin(
                cleanQuery, cleanCategory, active, pageable(page, size));

        return PageResponse.from(result, ProductResponse::from);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = findCategory(request.categoryId());

        Product product = Product.builder()
                .name(request.name().trim())
                .slug(uniqueSlug(SlugUtils.slugify(request.name())))
                .description(request.description().trim())
                .price(request.price())
                .comparePrice(request.comparePrice())
                .stock(request.stock())
                .sku(normalizeSku(request.sku(), null))
                .category(category)
                .imageUrl(normalize(request.imageUrl()))
                .active(request.active() == null || request.active())
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = find(id);
        Category category = findCategory(request.categoryId());
        String newSlug = SlugUtils.slugify(request.name());

        product.setName(request.name().trim());
        if (!newSlug.equals(product.getSlug())) {
            product.setSlug(uniqueSlug(newSlug));
        }
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setComparePrice(request.comparePrice());
        product.setStock(request.stock());
        product.setSku(normalizeSku(request.sku(), id));
        product.setCategory(category);
        product.setImageUrl(normalize(request.imageUrl()));
        if (request.active() != null) {
            product.setActive(request.active());
        }

        return ProductResponse.from(product);
    }

    /** Baja lógica: conserva el histórico de pedidos que referencian el producto. */
    @Transactional
    public void deactivate(Long id) {
        Product product = find(id);
        product.setActive(false);
    }

    /** Ajuste directo de inventario con bloqueo pesimista (consistente con el checkout). */
    @Transactional
    public ProductResponse adjustStock(Long id, int newStock) {
        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        product.setStock(newStock);
        return ProductResponse.from(product);
    }

    // ----------------------------------------------------------------- helpers

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    private String uniqueSlug(String base) {
        String slug = StringUtils.hasText(base) ? base : "producto";
        int suffix = 2;

        while (productRepository.existsBySlug(slug)) {
            slug = (StringUtils.hasText(base) ? base : "producto") + "-" + suffix++;
        }

        return slug;
    }

    private String normalizeSku(String sku, Long excludeId) {
        String value = normalize(sku);
        if (value == null) {
            return null;
        }

        boolean duplicated = excludeId == null
                ? productRepository.existsBySku(value)
                : productRepository.existsBySkuAndIdNot(value, excludeId);

        if (duplicated) {
            throw new DuplicateResourceException("Ya existe un producto con el SKU " + value);
        }
        return value;
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static Pageable pageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /** Expuesto para el checkout: total con dos decimales. */
    public static BigDecimal normalizeMoney(BigDecimal value) {
        return value.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}

package com.uiwave.ecommerce.catalog;

import com.uiwave.ecommerce.catalog.dto.CategoryRequest;
import com.uiwave.ecommerce.catalog.dto.CategoryResponse;
import com.uiwave.ecommerce.catalog.model.Category;
import com.uiwave.ecommerce.catalog.repository.ProductRepository;
import com.uiwave.ecommerce.category.repository.CategoryRepository;
import com.uiwave.ecommerce.common.exception.DuplicateResourceException;
import com.uiwave.ecommerce.common.exception.ResourceNotFoundException;
import com.uiwave.ecommerce.common.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> listAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.name().trim())
                .slug(uniqueSlug(SlugUtils.slugify(request.name())))
                .description(normalize(request.description()))
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = find(id);
        String newSlug = SlugUtils.slugify(request.name());

        category.setName(request.name().trim());
        if (!newSlug.equals(category.getSlug())) {
            category.setSlug(uniqueSlug(newSlug));
        }
        category.setDescription(normalize(request.description()));

        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = find(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new DuplicateResourceException(
                    "No se puede eliminar una categoría con productos asociados");
        }

        categoryRepository.delete(category);
    }

    private Category find(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    private String uniqueSlug(String base) {
        String slug = StringUtils.hasText(base) ? base : "categoria";
        int suffix = 2;

        while (categoryRepository.existsBySlug(slug)) {
            slug = (StringUtils.hasText(base) ? base : "categoria") + "-" + suffix++;
        }

        return slug;
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}

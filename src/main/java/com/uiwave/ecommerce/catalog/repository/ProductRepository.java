package com.uiwave.ecommerce.catalog.repository;

import com.uiwave.ecommerce.catalog.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlugAndActiveTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoryId(Long categoryId);

    long countByActiveTrue();

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueAndCategorySlug(String categorySlug, Pageable pageable);

    @Query("""
            select p from Product p
            where p.active = true
              and (lower(p.name) like lower(concat('%', :q, '%'))
                or lower(p.description) like lower(concat('%', :q, '%'))
                or lower(p.slug) like lower(concat('%', :q, '%')))
            """)
    Page<Product> searchActive(@Param("q") String q, Pageable pageable);

    @Query("""
            select p from Product p
            where p.active = true
              and p.category.slug = :categorySlug
              and (lower(p.name) like lower(concat('%', :q, '%'))
                or lower(p.description) like lower(concat('%', :q, '%'))
                or lower(p.slug) like lower(concat('%', :q, '%')))
            """)
    Page<Product> searchActiveByCategory(
            @Param("q") String q,
            @Param("categorySlug") String categorySlug,
            Pageable pageable);

    /** Bloquea la fila del producto durante el checkout para evitar sobreventa. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    /** Listado administrativo con búsqueda, categoría y estado. */
    @Query("""
            select p from Product p
            where (:q is null
                    or lower(p.name) like lower(concat('%', :q, '%'))
                    or lower(p.slug) like lower(concat('%', :q, '%')))
              and (:categorySlug is null or p.category.slug = :categorySlug)
              and (:active is null or p.active = :active)
            """)
    Page<Product> searchAdmin(
            @Param("q") String q,
            @Param("categorySlug") String categorySlug,
            @Param("active") Boolean active,
            Pageable pageable);

    List<Product> findTop5ByActiveTrueAndStockLessThanEqualOrderByStockAsc(int stock);
}

package com.uiwave.ecommerce.order.repository;

import com.uiwave.ecommerce.order.model.OrderStatus;
import com.uiwave.ecommerce.order.model.ShopOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

    Page<ShopOrder> findByUserId(Long userId, Pageable pageable);

    Optional<ShopOrder> findByIdAndUserId(Long id, Long userId);

    long countByStatus(OrderStatus status);

    List<ShopOrder> findByStatusNotAndCreatedAtAfter(OrderStatus status, Instant after);

    @Query("select coalesce(sum(o.total), 0) from ShopOrder o where o.status <> :excluded")
    BigDecimal sumTotalExcluding(@Param("excluded") OrderStatus excluded);

    /** Listado administrativo con búsqueda por email/nombre y filtro por estado. */
    @Query("""
            select o from ShopOrder o
            where (:q is null
                    or lower(o.user.email) like lower(concat('%', :q, '%'))
                    or lower(o.fullName) like lower(concat('%', :q, '%')))
              and (:status is null or o.status = :status)
            """)
    Page<ShopOrder> searchAdmin(
            @Param("q") String q,
            @Param("status") OrderStatus status,
            Pageable pageable);
}

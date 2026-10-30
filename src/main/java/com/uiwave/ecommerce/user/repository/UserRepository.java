package com.uiwave.ecommerce.user.repository;

import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select u from User u
            where (:q is null
                    or lower(u.email) like lower(concat('%', :q, '%'))
                    or lower(u.fullName) like lower(concat('%', :q, '%')))
              and (:role is null or u.role = :role)
            """)
    Page<User> search(@Param("q") String q, @Param("role") Role role, Pageable pageable);
}

package com.uiwave.ecommerce.user.service;

import com.uiwave.ecommerce.auth.dto.UserResponse;
import com.uiwave.ecommerce.common.dto.PageResponse;
import com.uiwave.ecommerce.common.exception.BadRequestException;
import com.uiwave.ecommerce.common.exception.ResourceNotFoundException;
import com.uiwave.ecommerce.user.dto.UpdateUserRequest;
import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.model.User;
import com.uiwave.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Consulta y administración de usuarios. El acceso está restringido en el
 * controlador mediante {@code @PreAuthorize("hasRole('ADMIN')")} y la regla
 * de seguridad {@code /api/admin/**}.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** Listado paginado con búsqueda por email/nombre y filtro por rol. */
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsers(String query, Role role, int page, int size) {
        String cleanQuery = StringUtils.hasText(query) ? query.trim() : null;
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return PageResponse.from(userRepository.search(cleanQuery, role, pageable), UserResponse::from);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, String actorEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        boolean isSelf = user.getEmail().equalsIgnoreCase(actorEmail);

        if (isSelf && request.role() != null && request.role() != Role.ADMIN) {
            throw new BadRequestException("No puedes quitarte el rol de administrador");
        }
        if (isSelf && request.enabled() != null && !request.enabled()) {
            throw new BadRequestException("No puedes desactivar tu propia cuenta");
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }

        return UserResponse.from(user);
    }
}

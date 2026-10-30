package com.uiwave.ecommerce.auth;

import com.uiwave.ecommerce.auth.dto.AuthResponse;
import com.uiwave.ecommerce.auth.dto.LoginRequest;
import com.uiwave.ecommerce.auth.dto.RefreshRequest;
import com.uiwave.ecommerce.auth.dto.RegisterRequest;
import com.uiwave.ecommerce.auth.dto.UpdateProfileRequest;
import com.uiwave.ecommerce.auth.dto.UserResponse;
import com.uiwave.ecommerce.common.exception.DuplicateResourceException;
import com.uiwave.ecommerce.common.exception.InvalidTokenException;
import com.uiwave.ecommerce.common.exception.ResourceNotFoundException;
import com.uiwave.ecommerce.security.JwtProperties;
import com.uiwave.ecommerce.security.JwtService;
import com.uiwave.ecommerce.user.model.RefreshToken;
import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.model.User;
import com.uiwave.ecommerce.user.repository.RefreshTokenRepository;
import com.uiwave.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int REFRESH_TOKEN_BYTES = 64;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Ya existe una cuenta con el email " + email);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName().trim())
                .imageUrl(normalizeImageUrl(request.imageUrl()))
                .role(Role.USER)
                .enabled(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizeEmail(request.email()), request.password()));

        User user = (User) authentication.getPrincipal();
        return issueTokens(user);
    }

    /**
     * Rota el refresh token: el presentado se revoca y se emite uno nuevo.
     * Si se detecta reuso de un token ya revocado, se revocan todos los tokens del
     * usuario.
     */
    @Transactional(noRollbackFor = InvalidTokenException.class)
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(request.refreshToken()))
                .orElseThrow(() -> new InvalidTokenException("Refresh token inválido"));

        if (stored.isRevoked()) {
            refreshTokenRepository.revokeAllByUser(stored.getUser());
            throw new InvalidTokenException("Refresh token revocado. Inicia sesión nuevamente.");
        }

        if (stored.isExpired()) {
            stored.setRevoked(true);
            throw new InvalidTokenException("Refresh token expirado. Inicia sesión nuevamente.");
        }

        stored.setRevoked(true);
        return issueTokens(stored.getUser());
    }

    @Transactional
    public void logout(String email, RefreshRequest request) {
        refreshTokenRepository.findByTokenHash(hash(request.refreshToken()))
                .filter(token -> token.getUser().getEmail().equalsIgnoreCase(email))
                .ifPresent(token -> token.setRevoked(true));
    }

    @Transactional(readOnly = true)
    public UserResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UserResponse.from(user);
    }

    /**
     * Actualiza el perfil del usuario autenticado. Si imageUrl viene null, se
     * conserva
     * la imagen actual; si viene vacía, se elimina.
     */
    @Transactional
    public UserResponse updateMe(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        user.setFullName(request.fullName().trim());
        if (request.imageUrl() != null) {
            user.setImageUrl(normalizeImageUrl(request.imageUrl()));
        }

        return UserResponse.from(user);
    }

    private AuthResponse issueTokens(User user) {
        String rawRefreshToken = generateRawRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hash(rawRefreshToken))
                .user(user)
                .expiresAt(Instant.now().plus(jwtProperties.refreshExpiration()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponse(
                accessToken,
                rawRefreshToken,
                "Bearer",
                jwtProperties.accessExpiration().toSeconds(),
                UserResponse.from(user));
    }

    private static String generateRawRefreshToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo SHA-256 no disponible", ex);
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeImageUrl(String imageUrl) {
        return StringUtils.hasText(imageUrl) ? imageUrl.trim() : null;
    }
}

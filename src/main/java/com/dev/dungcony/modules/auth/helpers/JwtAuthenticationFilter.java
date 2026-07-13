package com.dev.dungcony.modules.auth.helpers;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.modules.auth.enums.Role;
import com.dev.dungcony.modules.auth.services.interfaces.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT Authentication Filter
 * Filter này sẽ intercept mọi request và validate JWT token trong header
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy JWT token từ header
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);

            // 2. Kiểm tra header có tồn tại và bắt đầu với "Bearer "
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                logger.debug("No JWT token found in request headers");
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Extract token (bỏ "Bearer " prefix)
            String token = authHeader.substring(BEARER_PREFIX.length());

            // 4. Validate token
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // 5. Extract thông tin từ token
            String username = jwtService.extractUsername(token);
            Integer userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);
            Role role = jwtService.extractRole(token);
            UUID userUuid = jwtService.extractUserUuid(token);

            logger.debug("Extracted from token - username: {}, userId: {}, role: {}", username, userId, role);

            if (username == null || userId == null) {
                logger.warn("Cannot extract user info from token");
                filterChain.doFilter(request, response);
                return;
            }
            AccountDetails accountDetails = new AccountDetails(userId, username, email, role, userUuid);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    accountDetails,
                    null,
                    accountDetails.getAuthorities());

            // SET VÀO SECURITY CONTEXT
            // Từ đây về sau, mọi nơi trong request này đều biết user đã login
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("JWT authentication successful for user: {}", username);

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // 7. Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Có thể override method này để skip filter cho một số path nhất định
     * Ví dụ: public endpoints, static resources, etc.
     */
    @Override
    protected boolean shouldNotFilter(@SuppressWarnings("null") HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Skip filter cho các public endpoints
        return path.startsWith("/v1/api/public/auth/") ||
                path.startsWith("/v1/api/account/check/exists-email") ||
                path.startsWith("/v1/api/account/check/exists-username") ||
                path.startsWith("/v1/api/test/") ||
                path.equals("/") ||
                path.equals("/log") ||
                path.startsWith("/error") ||
                path.startsWith("/swagger-ui/") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs/") ||
                path.equals("/actuator/health");
    }
}

package com.emras.auth.security;

import com.emras.auth.constant.SecurityConstant;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT Authentication Filter — runs ONCE per request before any controller.
 *
 * Flow:
 *  1. Skip public endpoints entirely (no JWT needed).
 *  2. Extract "Bearer <token>" from the Authorization header.
 *  3. Validate the token via JwtService.
 *  4. On success → set UsernamePasswordAuthenticationToken in SecurityContext.
 *  5. On failure → do nothing (SecurityContext stays empty).
 *     Spring Security will then return 401 when the request hits a protected route.
 *
 * Why do nothing on failure instead of writing a 401 response here?
 * Because the AuthenticationEntryPoint (configured in SecurityConfig) handles
 * that responsibility — it produces a proper ApiResponse.failure() JSON body.
 * The filter's only job is to populate the SecurityContext when possible.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // ── Step 1: Skip public endpoints ─────────────────────────────────
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 2: Extract token from header ─────────────────────────────
        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 3: Validate and parse token ──────────────────────────────
        try {
            Claims claims = jwtService.validateAccessToken(token);

            // ── Step 4: Build authentication object ───────────────────────
            String email   = claims.getSubject();
            Long   userId  = claims.get("userId", Long.class);
            String roles   = claims.get("roles", String.class);

            // Put userId in MDC so it appears in every log line for this request
            MDC.put("userId", String.valueOf(userId));

            List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT authenticated: userId={}, email={}, roles={}", userId, email, roles);

        } catch (Exception ex) {
            // Token is invalid / expired — log at debug level and continue.
            // SecurityContext stays empty → Spring Security returns 401 via EntryPoint.
            log.debug("JWT validation failed for request [{}]: {}",
                    request.getRequestURI(), ex.getMessage());
        } finally {
            MDC.remove("userId");
        }

        filterChain.doFilter(request, response);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstant.AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            return header.substring(SecurityConstant.TOKEN_PREFIX.length());
        }
        return null;
    }

    private boolean isPublicEndpoint(String requestUri) {
        return Arrays.stream(SecurityConstant.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
}
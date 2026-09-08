package com.stmartinviersen.tuetenverkauf.auth;

import com.stmartinviersen.tuetenverkauf.exceptions.InvalidTokenException;
import com.stmartinviersen.tuetenverkauf.user.model.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            Claims claims = authService.parseToken(token);

            String userId = claims.getSubject();
            if (userId == null || userId.isBlank()) {
                throw new InvalidTokenException("Token has no subject");
            }

            List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(userId, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Object rawRoles = claims.get("roles");
        if (!(rawRoles instanceof Collection)) {
            return authorities;
        }

        for (Object rawRole : (Collection<?>) rawRoles) {

            if (rawRole == null) {
                continue;
            }

            try {
                UserRole userRole = UserRole.valueOf(rawRole.toString());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
            } catch (IllegalArgumentException e) {
                // Role name that no longer exists in the enum (old token) -> Ignored
            }
        }

        return authorities;
    }
}
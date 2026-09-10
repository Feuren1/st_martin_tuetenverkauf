package com.stmartinviersen.tuetenverkauf.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.stmartinviersen.tuetenverkauf.user.mapper.UserMapper;
import com.stmartinviersen.tuetenverkauf.user.model.User;
import com.stmartinviersen.tuetenverkauf.user.model.UserRole;
import com.stmartinviersen.tuetenverkauf.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


class JwtAuthenticationFilterTest {

    private static final String SECRET =
            Base64.getEncoder().encodeToString("filter-test-secret-filter-test-secret-32".getBytes());

    private AuthService authService;
    private JwtAuthenticationFilter filter;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        authService = new AuthService(new BCryptPasswordEncoder(), mock(UserRepository.class), mock(UserMapper.class));
        ReflectionTestUtils.setField(authService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(authService, "tokenTtl", Duration.ofMinutes(15));
        ReflectionTestUtils.invokeMethod(authService, "init");

        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        filter = new JwtAuthenticationFilter(authService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthorizationHeader_continuesChainWithoutAuthentication() throws Exception {
        RecordingFilterChain chain = doFilter(null);

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void headerWithoutBearerPrefix_continuesChainWithoutAuthentication() throws Exception {
        RecordingFilterChain chain = doFilter("Basic dXNlcjpwYXNz");

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validToken_setsAuthenticationWithSubjectAndAuthorities() throws Exception {
        User user = User.builder().id(42L).roles(Set.of(UserRole.VEREINSMITGLIED)).build();
        String token = mintToken(user);

        RecordingFilterChain chain = doFilter("Bearer " + token);

        assertTrue(chain.invoked);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("42", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VEREINSMITGLIED")));
    }

    @Test
    void expiredToken_clearsContextAndContinuesChain() throws Exception {
        ReflectionTestUtils.setField(authService, "tokenTtl", Duration.ofSeconds(-1));
        User user = User.builder().id(1L).roles(Set.of(UserRole.KAEUFER)).build();
        String token = mintToken(user);

        RecordingFilterChain chain = doFilter("Bearer " + token);

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tamperedSignature_clearsContextAndContinuesChain() throws Exception {
        User user = User.builder().id(1L).roles(Set.of(UserRole.KAEUFER)).build();
        String token = mintToken(user);
        String tampered = token.substring(0, token.length() - 4) + "abcd";

        RecordingFilterChain chain = doFilter("Bearer " + tampered);

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void malformedTokenString_clearsContextAndContinuesChain() throws Exception {
        RecordingFilterChain chain = doFilter("Bearer not-a-jwt-at-all");

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void blankSubject_clearsContextAndContinuesChain() throws Exception {
        String token = Jwts.builder()
                .subject(" ")
                .claim("roles", List.of("KAEUFER"))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .signWith(secretKey)
                .compact();

        RecordingFilterChain chain = doFilter("Bearer " + token);

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void unknownRoleInToken_isIgnoredButKnownRolesStillApply() throws Exception {
        String token = Jwts.builder()
                .subject("7")
                .claim("roles", List.of("KAEUFER", "SUPERADMIN_LEGACY"))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .signWith(secretKey)
                .compact();

        RecordingFilterChain chain = doFilter("Bearer " + token);

        assertTrue(chain.invoked);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_KAEUFER")));
    }

    private String mintToken(User user) {
        return ReflectionTestUtils.invokeMethod(authService, "issueToken", user);
    }

    private RecordingFilterChain doFilter(String authorizationHeaderValue) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (authorizationHeaderValue != null) {
            request.addHeader("Authorization", authorizationHeaderValue);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain chain = new RecordingFilterChain();

        filter.doFilter(request, response, chain);

        return chain;
    }

    private static class RecordingFilterChain implements FilterChain {
        private boolean invoked = false;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            invoked = true;
        }
    }
}

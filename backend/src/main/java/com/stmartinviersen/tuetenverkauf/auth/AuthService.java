package com.stmartinviersen.tuetenverkauf.auth;

import com.stmartinviersen.tuetenverkauf.exceptions.BadCredentialsException;
import com.stmartinviersen.tuetenverkauf.exceptions.EmailAlreadyExistsException;
import com.stmartinviersen.tuetenverkauf.exceptions.InvalidTokenException;
import com.stmartinviersen.tuetenverkauf.exceptions.TokenExpiredException;
import com.stmartinviersen.tuetenverkauf.user.mapper.UserMapper;
import com.stmartinviersen.tuetenverkauf.user.model.LoginRequest;
import com.stmartinviersen.tuetenverkauf.user.model.RegisterRequest;
import com.stmartinviersen.tuetenverkauf.user.model.UserRole;
import com.stmartinviersen.tuetenverkauf.user.model.User;
import com.stmartinviersen.tuetenverkauf.user.model.UserDTO;
import com.stmartinviersen.tuetenverkauf.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.ttl:PT12H}")
    private Duration tokenTtl;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (DecodingException e) {
            throw new IllegalStateException(
                    "jwt.secret must be Base64. Generate one with: openssl rand -base64 32", e);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret must decode to at least 32 bytes, got " + keyBytes.length);
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public UserDTO registerUser(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }

        User user = userMapper.toUser(request);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(UserRole.KAEUFER));

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return userMapper.toUserDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest loginRequest) {
        String email = normalizeEmail(loginRequest.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return issueToken(user);
    }

    private String issueToken(User user) {
        List<String> roleNames = new ArrayList<>();

        if (user.getRoles() != null) {
            for (UserRole role : user.getRoles()) {
                roleNames.add(role.name());
            }
        }
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", roleNames)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenTtl)))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
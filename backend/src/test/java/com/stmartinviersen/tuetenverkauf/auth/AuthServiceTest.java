package com.stmartinviersen.tuetenverkauf.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.stmartinviersen.tuetenverkauf.exceptions.BadCredentialsException;
import com.stmartinviersen.tuetenverkauf.exceptions.EmailAlreadyExistsException;
import com.stmartinviersen.tuetenverkauf.user.mapper.UserMapper;
import com.stmartinviersen.tuetenverkauf.user.model.LoginRequest;
import com.stmartinviersen.tuetenverkauf.user.model.RegisterRequest;
import com.stmartinviersen.tuetenverkauf.user.model.User;
import com.stmartinviersen.tuetenverkauf.user.model.UserDTO;
import com.stmartinviersen.tuetenverkauf.user.model.UserRole;
import com.stmartinviersen.tuetenverkauf.user.repository.UserRepository;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(passwordEncoder, userRepository, userMapper);
        ReflectionTestUtils.setField(authService, "jwtSecret",
                Base64.getEncoder().encodeToString("test-secret-test-secret-test-secret-32".getBytes()));
        ReflectionTestUtils.setField(authService, "tokenTtl", Duration.ofMinutes(15));
        ReflectionTestUtils.invokeMethod(authService, "init");
    }

    @Test
    void registerUserAssignsDefaultKaeuferRole() {
        RegisterRequest request = new RegisterRequest("Max", "Mustermann", "max@example.com", "supersecret");
        User mappedUser = User.builder().firstName("Max").lastName("Mustermann").build();

        when(userRepository.existsByEmail("max@example.com")).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(mappedUser);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserDTO(any(User.class)))
                .thenReturn(new UserDTO(1L, "max@example.com", "Max", "Mustermann", Set.of(UserRole.KAEUFER)));

        authService.registerUser(request);

        assertEquals(Set.of(UserRole.KAEUFER), mappedUser.getRoles());
    }

    @Test
    void registerUserThrowsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Max", "Mustermann", "max@example.com", "supersecret");
        when(userRepository.existsByEmail("max@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerUser(request));
    }

    @Test
    void loginThrowsOnUnknownEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("unknown@example.com", "whatever")));
    }

    @Test
    void loginThrowsOnWrongPassword() {
        User user = User.builder()
                .email("max@example.com")
                .passwordHash(passwordEncoder.encode("correct-password"))
                .roles(Set.of(UserRole.KAEUFER))
                .build();
        when(userRepository.findByEmail("max@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("max@example.com", "wrong-password")));
    }

    @Test
    void loginReturnsTokenOnValidCredentials() {
        User user = User.builder()
                .id(1L)
                .email("max@example.com")
                .passwordHash(passwordEncoder.encode("correct-password"))
                .roles(Set.of(UserRole.KAEUFER))
                .build();
        when(userRepository.findByEmail("max@example.com")).thenReturn(Optional.of(user));

        String token = authService.login(new LoginRequest("max@example.com", "correct-password"));

        assertTrue(token != null && !token.isBlank());
    }
}

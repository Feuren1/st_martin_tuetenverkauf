package com.stmartinviersen.tuetenverkauf.auth;

import com.stmartinviersen.tuetenverkauf.exceptions.ErrorResponse;
import com.stmartinviersen.tuetenverkauf.user.model.*;
import com.stmartinviersen.tuetenverkauf.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerHappyPath() {
        RegisterRequest request = new RegisterRequest("Max", "Mustermann", "max@example.com", "supersecret");

        ResponseEntity<UserDTO> response = restTemplate.postForEntity("/auth/register", request, UserDTO.class);
        SoftAssertions softly = new SoftAssertions();
        UserDTO body = response.getBody();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(body).isNotNull();
        softly.assertThat(body.id()).isNotNull();
        softly.assertThat(body.email()).isEqualTo("max@example.com");
        softly.assertThat(body.roles()).containsExactly(UserRole.KAEUFER);
        softly.assertAll();
    }

    @Test
    void registerDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Max", "Mustermann", "dup@example.com", "supersecret");
        restTemplate.postForEntity("/auth/register", request, UserDTO.class);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/auth/register", request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void registerInvalidPayload() {
        RegisterRequest request = new RegisterRequest("", "", "not-an-email", "short");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/auth/register", request, ErrorResponse.class);
        SoftAssertions softly = new SoftAssertions();
        ErrorResponse body = response.getBody();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        softly.assertThat(body).isNotNull();
        softly.assertThat(body.fieldErrors()).isNotNull();
        softly.assertThat(body.fieldErrors()).containsKey("email");
        softly.assertThat(body.fieldErrors()).containsKey("password");
        softly.assertAll();
    }

    @Test
    void loginHappyPath() {
        RegisterRequest registerRequest = new RegisterRequest("Max", "Mustermann", "login@example.com", "supersecret");
        UserDTO registered = restTemplate.postForEntity("/auth/register", registerRequest, UserDTO.class).getBody();
        assertNotNull(registered);

        LoginRequest loginRequest = new LoginRequest("login@example.com", "supersecret");
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/auth/login", loginRequest, LoginResponse.class);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        LoginResponse body = response.getBody();
        softly.assertThat(body).isNotNull();
        softly.assertThat(body.token()).isNotNull();
        softly.assertThat(body.token()).isNotBlank();

        Claims claims = parseToken(body.token());
        softly.assertThat(registered.id().toString()).isEqualTo(claims.getSubject());
        softly.assertThat(List.of("KAEUFER")).isEqualTo(claims.get("roles"));
        softly.assertAll();
    }

    @Test
    void loginWrongPassword() {
        restTemplate.postForEntity("/auth/register",
                new RegisterRequest("Max", "Mustermann", "wrongpw@example.com", "supersecret"), UserDTO.class);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/auth/login", new LoginRequest("wrongpw@example.com", "totally-wrong"), ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginUnknownEmail() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/auth/login", new LoginRequest("ghost@example.com", "whatever"), ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginInvalidPayload() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/auth/login", new LoginRequest("", ""), ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}

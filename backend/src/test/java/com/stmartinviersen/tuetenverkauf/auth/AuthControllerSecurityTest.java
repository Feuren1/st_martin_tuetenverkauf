package com.stmartinviersen.tuetenverkauf.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stmartinviersen.tuetenverkauf.user.model.LoginRequest;
import com.stmartinviersen.tuetenverkauf.user.model.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, ServletWebSecurityAutoConfiguration.class})
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerIsAccessibleWithoutAuthentication() throws Exception {
        RegisterRequest request = new RegisterRequest("Max", "Mustermann", "max@example.com", "supersecret");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void loginIsAccessibleWithoutAuthentication() throws Exception {
        LoginRequest request = new LoginRequest("max@example.com", "supersecret");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void unmappedEndpointIsDeniedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/some/unmapped/path"))
                .andExpect(status().isForbidden());
    }
}

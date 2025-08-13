package learn.spring.newsaggregator.controller;

import learn.spring.newsaggregator.dto.JwtResponseDto;
import learn.spring.newsaggregator.dto.UserLoginDto;
import learn.spring.newsaggregator.dto.UserRegistrationDto;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.exception.UserAlreadyExistsException;
import learn.spring.newsaggregator.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuthController with Java 24 compatibility.
 * Uses @WebMvcTest for focused web layer testing.
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private UserRegistrationDto registrationDto;
    private UserLoginDto loginDto;
    private User user;
    private JwtResponseDto jwtResponseDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");

        loginDto = new UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        user = new User("testuser", "test@example.com", "encodedPassword");
        user.setId(1L);

        jwtResponseDto = new JwtResponseDto("test-jwt-token", "testuser", "test@example.com");
    }

    @Test
    @WithMockUser
    void registerUser_Success() throws Exception {
        // Given
        when(authService.registerUser(any(UserRegistrationDto.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser
    void registerUser_UsernameAlreadyExists() throws Exception {
        // Given
        when(authService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException("Username is already taken!"));

        // When & Then
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username is already taken!"));

        verify(authService).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser
    void registerUser_EmailAlreadyExists() throws Exception {
        // Given
        when(authService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException("Email is already in use!"));

        // When & Then
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use!"));

        verify(authService).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser
    void registerUser_InvalidInput_MissingUsername() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser
    void registerUser_InvalidInput_InvalidEmail() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "email": "invalid-email",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser
    void loginUser_Success() throws Exception {
        // Given
        when(authService.authenticateUser(any(UserLoginDto.class))).thenReturn(jwtResponseDto);

        // When & Then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService).authenticateUser(any(UserLoginDto.class));
    }

    @Test
    @WithMockUser
    void loginUser_InvalidCredentials() throws Exception {
        // Given
        when(authService.authenticateUser(any(UserLoginDto.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "wrongpassword"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(authService).authenticateUser(any(UserLoginDto.class));
    }

    @Test
    @WithMockUser
    void loginUser_InvalidInput_MissingPassword() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticateUser(any(UserLoginDto.class));
    }

    @Test
    @WithMockUser
    void loginUser_EmptyRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticateUser(any(UserLoginDto.class));
    }
}

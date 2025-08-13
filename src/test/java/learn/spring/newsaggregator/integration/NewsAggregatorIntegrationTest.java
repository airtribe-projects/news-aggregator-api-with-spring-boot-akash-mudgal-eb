package learn.spring.newsaggregator.integration;

import learn.spring.newsaggregator.dto.UserRegistrationDto;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the News Aggregator API with Java 24 compatibility.
 * Tests the full application stack with in-memory database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class NewsAggregatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void userRegistrationFlow_Success() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("integrationtest");
        registrationDto.setEmail("integration@test.com");
        registrationDto.setPassword("password123");

        // When - Register user
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "integrationtest",
                                    "email": "integration@test.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andExpect(jsonPath("$.username").value("integrationtest"))
                .andExpect(jsonPath("$.email").value("integration@test.com"));

        // Then - Verify user was saved to database
        assertTrue(userRepository.existsByUsername("integrationtest"));
        assertTrue(userRepository.existsByEmail("integration@test.com"));

        User savedUser = userRepository.findByUsername("integrationtest").orElse(null);
        assertNotNull(savedUser);
        assertEquals("integrationtest", savedUser.getUsername());
        assertEquals("integration@test.com", savedUser.getEmail());
        assertNotNull(savedUser.getPassword()); // Should be encoded
        assertNotEquals("password123", savedUser.getPassword()); // Should not be plain text
    }

    @Test
    @WithMockUser
    void userRegistration_DuplicateUsername() throws Exception {
        // Given - Create existing user
        User existingUser = new User("duplicateuser", "existing@test.com", "encodedPassword");
        userRepository.save(existingUser);

        // When - Try to register with same username
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "duplicateuser",
                                    "email": "newemail@test.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username is already taken!"));

        // Then - Verify only one user exists
        assertEquals(1, userRepository.count());
    }

    @Test
    @WithMockUser
    void userRegistration_DuplicateEmail() throws Exception {
        // Given - Create existing user
        User existingUser = new User("existinguser", "duplicate@test.com", "encodedPassword");
        userRepository.save(existingUser);

        // When - Try to register with same email
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "newuser",
                                    "email": "duplicate@test.com",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use!"));

        // Then - Verify only one user exists
        assertEquals(1, userRepository.count());
    }

    @Test
    @WithMockUser
    void userRegistration_ValidationFailure() throws Exception {
        // When - Try to register with invalid data
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "",
                                    "email": "invalid-email",
                                    "password": "123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Then - Verify no user was created
        assertEquals(0, userRepository.count());
    }

    @Test
    @WithMockUser
    void userLogin_Success() throws Exception {
        // Given - Create and save a user first through registration
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "logintest",
                                    "email": "login@test.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk());

        // When - Login with correct credentials
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "logintest",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("logintest"))
                .andExpect(jsonPath("$.email").value("login@test.com"));
    }

    @Test
    @WithMockUser
    void completeUserJourney() throws Exception {
        // Step 1: Register a new user
        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "journeyuser",
                                    "email": "journey@test.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Step 2: Login with the new user
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "journeyuser",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("journeyuser"));

        // Verify user exists in database
        User user = userRepository.findByUsername("journeyuser").orElse(null);
        assertNotNull(user);
        assertEquals("journeyuser", user.getUsername());
        assertEquals("journey@test.com", user.getEmail());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
    }
}

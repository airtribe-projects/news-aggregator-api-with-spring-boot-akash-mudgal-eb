package learn.spring.newsaggregator.security;

import learn.spring.newsaggregator.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for JwtUtils with Java 24 compatibility.
 * Tests JWT token generation, validation, and extraction methods.
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtUtils jwtUtils;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        // Set up private fields using reflection
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyThatIsLongEnoughForHmacSha256Algorithm");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000); // 24 hours

        // Initialize the key after setting the secret
        jwtUtils.init();
    }

    @Test
    void generateJwtToken_Success() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ")); // JWT tokens start with this
        verify(authentication).getPrincipal();
    }

    @Test
    void getUserNameFromJwtToken_Success() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void validateJwtToken_ValidToken() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_MalformedToken() {
        // Given
        String malformedToken = "malformed-token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_ExpiredToken() {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1); // 1 millisecond
        jwtUtils.init(); // Reinitialize with new expiration

        when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtils.generateJwtToken(authentication);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_EmptyToken() {
        // When
        boolean isValid = jwtUtils.validateJwtToken("");

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_NullToken() {
        // When
        boolean isValid = jwtUtils.validateJwtToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getUserNameFromJwtToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtils.getUserNameFromJwtToken(invalidToken);
        });
    }

    @Test
    void generateJwtToken_NullAuthentication() {
        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtils.generateJwtToken(null);
        });
    }

    @Test
    void generateJwtToken_NullPrincipal() {
        // Given
        when(authentication.getPrincipal()).thenReturn(null);

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtils.generateJwtToken(authentication);
        });
    }

    @Test
    void tokenContainsCorrectClaims() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertEquals(user.getUsername(), extractedUsername);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void tokenExpirationIsSetCorrectly() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        // Token should be valid immediately after generation
    }
}

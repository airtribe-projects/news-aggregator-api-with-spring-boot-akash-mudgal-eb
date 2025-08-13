package learn.spring.newsaggregator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test to verify Java 24 compatibility.
 */
@SpringBootTest
@ActiveProfiles("test")
class NewsAggregatorApiApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
        // with Java 24 and all our configurations
    }
}

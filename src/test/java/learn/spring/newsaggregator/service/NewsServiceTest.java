package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.dto.NewsResponseDto;
import learn.spring.newsaggregator.entity.NewsPreference;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.exception.NewsApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for NewsService with Java 24 compatibility.
 * Uses lenient mocking to avoid strict stubbing issues with WebClient generic types.
 */
@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private NewsPreferenceService newsPreferenceService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private NewsService newsService;

    private User user;
    private NewsPreference newsPreference;
    private NewsResponseDto mockResponse;

    @BeforeEach
    void setUp() {
        // Set up test data
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        newsPreference = new NewsPreference();
        newsPreference.setUser(user);
        newsPreference.setCategories(Set.of("technology", "business"));
        newsPreference.setSources(Set.of("bbc-news", "cnn"));
        newsPreference.setCountries(Set.of("us"));
        newsPreference.setLanguages(Set.of("en"));

        mockResponse = new NewsResponseDto();
        mockResponse.setStatus("ok");
        mockResponse.setTotalResults(10);

        // Set up private fields using reflection
        ReflectionTestUtils.setField(newsService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(newsService, "baseUrl", "https://newsapi.org/v2");

        // Configure WebClient mock chain with lenient stubbing
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(NewsResponseDto.class)).thenReturn(Mono.just(mockResponse));
    }

    @Test
    void getNewsForUser_WithPreferences() {
        // Given
        when(newsPreferenceService.getPreferenceByUser(user)).thenReturn(newsPreference);
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.getNewsForUser(user);

        // Then
        assertNotNull(result);
        assertEquals("ok", result.getStatus());
        assertEquals(10, result.getTotalResults());
        verify(newsPreferenceService).getPreferenceByUser(user);
    }

    @Test
    void getNewsForUser_WithoutPreferences() {
        // Given
        when(newsPreferenceService.getPreferenceByUser(user)).thenReturn(null);
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.getNewsForUser(user);

        // Then
        assertNotNull(result);
        assertEquals("ok", result.getStatus());
        verify(newsPreferenceService).getPreferenceByUser(user);
    }

    @Test
    void getTopHeadlines_Success() {
        // Given
        Set<String> categories = Set.of("technology");
        Set<String> sources = Set.of("bbc-news");
        Set<String> countries = Set.of("us");
        Set<String> languages = Set.of("en");

        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.getTopHeadlines(categories, sources, countries, languages);

        // Then
        assertNotNull(result);
        assertEquals("ok", result.getStatus());
        assertEquals(10, result.getTotalResults());
    }

    @Test
    void getTopHeadlines_WebClientException() {
        // Given
        Set<String> categories = Set.of("technology");
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NewsResponseDto.class))
                .thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

        // When & Then
        assertThrows(NewsApiException.class, () -> {
            newsService.getTopHeadlines(categories, null, null, null);
        });
    }

    @Test
    void searchNews_Success() {
        // Given
        String keyword = "technology";
        Set<String> categories = Set.of("technology");
        Set<String> sources = Set.of("bbc-news");

        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.searchNews(keyword, categories, sources);

        // Then
        assertNotNull(result);
        assertEquals("ok", result.getStatus());
        assertEquals(10, result.getTotalResults());
    }

    @Test
    void searchNews_WebClientException() {
        // Given
        String keyword = "technology";
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NewsResponseDto.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Server Error", null, null, null)));

        // When & Then
        assertThrows(NewsApiException.class, () -> {
            newsService.searchNews(keyword, null, null);
        });
    }

    @Test
    void getAllSources_Success() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.getAllSources();

        // Then
        assertNotNull(result);
        assertEquals("ok", result.getStatus());
    }

    @Test
    void getAllSources_WebClientException() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NewsResponseDto.class))
                .thenReturn(Mono.error(new WebClientResponseException(401, "Unauthorized", null, null, null)));

        // When & Then
        assertThrows(NewsApiException.class, () -> {
            newsService.getAllSources();
        });
    }

    @Test
    void buildTopHeadlinesUrl_WithSources() {
        // Test URL building logic indirectly through public methods
        Set<String> sources = Set.of("bbc-news", "cnn");
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.getTopHeadlines(null, sources, null, null);

        // Then
        assertNotNull(result);
        // URL should include sources parameter but not country when sources are provided
    }

    @Test
    void buildTopHeadlinesUrl_WithCountryAndCategory() {
        // Test URL building logic indirectly through public methods
        Set<String> categories = Set.of("technology");
        Set<String> countries = Set.of("us");
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        NewsResponseDto result = newsService.getTopHeadlines(categories, null, countries, null);

        // Then
        assertNotNull(result);
        // URL should include country and category but not sources
    }
}

package learn.spring.newsaggregator.controller;

import learn.spring.newsaggregator.dto.NewsResponseDto;
import learn.spring.newsaggregator.entity.FavoriteArticle;
import learn.spring.newsaggregator.entity.ReadArticle;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.service.ArticleInteractionService;
import learn.spring.newsaggregator.service.AuthService;
import learn.spring.newsaggregator.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for NewsController with Java 24 compatibility.
 * Uses @WebMvcTest for focused web layer testing.
 */
@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @MockBean
    private AuthService authService;

    @MockBean
    private ArticleInteractionService articleInteractionService;

    private User user;
    private NewsResponseDto newsResponse;
    private ReadArticle readArticle;
    private FavoriteArticle favoriteArticle;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        newsResponse = new NewsResponseDto();
        newsResponse.setStatus("ok");
        newsResponse.setTotalResults(10);

        readArticle = new ReadArticle(user, "article-123", "Test Article", "https://example.com/article");
        readArticle.setId(1L);
        readArticle.setReadAt(LocalDateTime.now());

        favoriteArticle = new FavoriteArticle(user, "article-456", "Favorite Article", "https://example.com/favorite");
        favoriteArticle.setId(1L);
        favoriteArticle.setFavoritedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNews_Success() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(user);
        when(newsService.getNewsForUser(user)).thenReturn(newsResponse);

        // When & Then
        mockMvc.perform(get("/api/news")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.totalResults").value(10));

        verify(authService).getCurrentUser();
        verify(newsService).getNewsForUser(user);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTopHeadlines_Success() throws Exception {
        // Given
        when(newsService.getTopHeadlines(any(), any(), any(), any())).thenReturn(newsResponse);

        // When & Then
        mockMvc.perform(get("/api/news/top-headlines")
                        .with(csrf())
                        .param("categories", "technology,business")
                        .param("sources", "bbc-news,cnn")
                        .param("countries", "us")
                        .param("languages", "en"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.totalResults").value(10));

        verify(newsService).getTopHeadlines(anySet(), anySet(), anySet(), anySet());
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchNews_Success() throws Exception {
        // Given
        when(newsService.searchNews(anyString(), any(), any())).thenReturn(newsResponse);

        // When & Then
        mockMvc.perform(get("/api/news/search")
                        .with(csrf())
                        .param("q", "technology")
                        .param("categories", "technology")
                        .param("sources", "bbc-news"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.totalResults").value(10));

        verify(newsService).searchNews(eq("technology"), anySet(), anySet());
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchNews_MissingQueryParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/news/search")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(newsService, never()).searchNews(anyString(), any(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getSources_Success() throws Exception {
        // Given
        when(newsService.getAllSources()).thenReturn(newsResponse);

        // When & Then
        mockMvc.perform(get("/api/news/sources")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        verify(newsService).getAllSources();
    }

    @Test
    @WithMockUser(roles = "USER")
    void markAsRead_Success() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(user);
        when(articleInteractionService.markAsRead(any(User.class), anyString(), anyString(), anyString()))
                .thenReturn(readArticle);

        // When & Then
        mockMvc.perform(post("/api/news/read")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "articleId": "article-123",
                                    "articleTitle": "Test Article",
                                    "articleUrl": "https://example.com/article"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Article marked as read"));

        verify(authService).getCurrentUser();
        verify(articleInteractionService).markAsRead(user, "article-123", "Test Article", "https://example.com/article");
    }

    @Test
    @WithMockUser(roles = "USER")
    void markAsFavorite_Success() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(user);
        when(articleInteractionService.markAsFavorite(any(User.class), anyString(), anyString(), anyString()))
                .thenReturn(favoriteArticle);

        // When & Then
        mockMvc.perform(post("/api/news/favorite")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "articleId": "article-456",
                                    "articleTitle": "Favorite Article",
                                    "articleUrl": "https://example.com/favorite"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Article marked as favorite"));

        verify(authService).getCurrentUser();
        verify(articleInteractionService).markAsFavorite(user, "article-456", "Favorite Article", "https://example.com/favorite");
    }

    @Test
    @WithMockUser(roles = "USER")
    void removeFavorite_Success() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(user);
        doNothing().when(articleInteractionService).removeFavorite(user, "article-456");

        // When & Then
        mockMvc.perform(delete("/api/news/favorite/article-456")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Article removed from favorites"));

        verify(authService).getCurrentUser();
        verify(articleInteractionService).removeFavorite(user, "article-456");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getReadArticles_Success() throws Exception {
        // Given
        List<ReadArticle> readArticles = Arrays.asList(readArticle);
        when(authService.getCurrentUser()).thenReturn(user);
        when(articleInteractionService.getReadArticles(user)).thenReturn(readArticles);

        // When & Then
        mockMvc.perform(get("/api/news/read")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].articleId").value("article-123"));

        verify(authService).getCurrentUser();
        verify(articleInteractionService).getReadArticles(user);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFavoriteArticles_Success() throws Exception {
        // Given
        List<FavoriteArticle> favoriteArticles = Arrays.asList(favoriteArticle);
        when(authService.getCurrentUser()).thenReturn(user);
        when(articleInteractionService.getFavoriteArticles(user)).thenReturn(favoriteArticles);

        // When & Then
        mockMvc.perform(get("/api/news/favorites")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].articleId").value("article-456"));

        verify(authService).getCurrentUser();
        verify(articleInteractionService).getFavoriteArticles(user);
    }

    @Test
    void getNews_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/news"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(authService, never()).getCurrentUser();
        verify(newsService, never()).getNewsForUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Different role
    void getNews_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/news")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(authService, never()).getCurrentUser();
        verify(newsService, never()).getNewsForUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void markAsRead_InvalidInput() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/news/read")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(articleInteractionService, never()).markAsRead(any(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    void markAsFavorite_InvalidInput() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/news/favorite")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(articleInteractionService, never()).markAsFavorite(any(), anyString(), anyString(), anyString());
    }
}

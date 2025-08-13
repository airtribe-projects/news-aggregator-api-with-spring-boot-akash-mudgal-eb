package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.entity.FavoriteArticle;
import learn.spring.newsaggregator.entity.ReadArticle;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.exception.ResourceNotFoundException;
import learn.spring.newsaggregator.repository.FavoriteArticleRepository;
import learn.spring.newsaggregator.repository.ReadArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleInteractionServiceTest {

    @Mock
    private ReadArticleRepository readArticleRepository;

    @Mock
    private FavoriteArticleRepository favoriteArticleRepository;

    @InjectMocks
    private ArticleInteractionService articleInteractionService;

    private User user;
    private String articleId;
    private String articleTitle;
    private String articleUrl;
    private ReadArticle readArticle;
    private FavoriteArticle favoriteArticle;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        articleId = "article-123";
        articleTitle = "Test Article Title";
        articleUrl = "https://example.com/article";

        readArticle = new ReadArticle(user, articleId, articleTitle, articleUrl);
        readArticle.setId(1L);
        readArticle.setReadAt(LocalDateTime.now());

        favoriteArticle = new FavoriteArticle(user, articleId, articleTitle, articleUrl);
        favoriteArticle.setId(1L);
        favoriteArticle.setFavoritedAt(LocalDateTime.now());
    }

    @Test
    void markAsRead_NewArticle() {
        // Given
        when(readArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(false);
        when(readArticleRepository.save(any(ReadArticle.class))).thenReturn(readArticle);

        // When
        ReadArticle result = articleInteractionService.markAsRead(user, articleId, articleTitle, articleUrl);

        // Then
        assertNotNull(result);
        assertEquals(articleId, result.getArticleId());
        assertEquals(articleTitle, result.getArticleTitle());
        assertEquals(articleUrl, result.getArticleUrl());
        assertEquals(user, result.getUser());
        verify(readArticleRepository).existsByUserAndArticleId(user, articleId);
        verify(readArticleRepository).save(any(ReadArticle.class));
    }

    @Test
    void markAsRead_ExistingArticle() {
        // Given
        when(readArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(true);
        when(readArticleRepository.findByUserAndArticleId(user, articleId)).thenReturn(Optional.of(readArticle));

        // When
        ReadArticle result = articleInteractionService.markAsRead(user, articleId, articleTitle, articleUrl);

        // Then
        assertNotNull(result);
        assertEquals(articleId, result.getArticleId());
        verify(readArticleRepository).existsByUserAndArticleId(user, articleId);
        verify(readArticleRepository, never()).save(any(ReadArticle.class));
        verify(readArticleRepository).findByUserAndArticleId(user, articleId);
    }

    @Test
    void markAsFavorite_NewArticle() {
        // Given
        when(favoriteArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(false);
        when(favoriteArticleRepository.save(any(FavoriteArticle.class))).thenReturn(favoriteArticle);

        // When
        FavoriteArticle result = articleInteractionService.markAsFavorite(user, articleId, articleTitle, articleUrl);

        // Then
        assertNotNull(result);
        assertEquals(articleId, result.getArticleId());
        assertEquals(articleTitle, result.getArticleTitle());
        assertEquals(articleUrl, result.getArticleUrl());
        assertEquals(user, result.getUser());
        verify(favoriteArticleRepository).existsByUserAndArticleId(user, articleId);
        verify(favoriteArticleRepository).save(any(FavoriteArticle.class));
    }

    @Test
    void markAsFavorite_ExistingArticle() {
        // Given
        when(favoriteArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(true);
        when(favoriteArticleRepository.findByUserAndArticleId(user, articleId)).thenReturn(Optional.of(favoriteArticle));

        // When
        FavoriteArticle result = articleInteractionService.markAsFavorite(user, articleId, articleTitle, articleUrl);

        // Then
        assertNotNull(result);
        assertEquals(articleId, result.getArticleId());
        verify(favoriteArticleRepository).existsByUserAndArticleId(user, articleId);
        verify(favoriteArticleRepository, never()).save(any(FavoriteArticle.class));
        verify(favoriteArticleRepository).findByUserAndArticleId(user, articleId);
    }

    @Test
    void removeFavorite_ExistingArticle() {
        // Given
        when(favoriteArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(true);

        // When
        articleInteractionService.removeFavorite(user, articleId);

        // Then
        verify(favoriteArticleRepository).existsByUserAndArticleId(user, articleId);
        verify(favoriteArticleRepository).deleteByUserAndArticleId(user, articleId);
    }

    @Test
    void removeFavorite_NonExistingArticle() {
        // Given
        when(favoriteArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> articleInteractionService.removeFavorite(user, articleId)
        );
        assertEquals("Favorite article not found for user", exception.getMessage());
        verify(favoriteArticleRepository).existsByUserAndArticleId(user, articleId);
        verify(favoriteArticleRepository, never()).deleteByUserAndArticleId(any(User.class), anyString());
    }

    @Test
    void getReadArticles() {
        // Given
        ReadArticle readArticle2 = new ReadArticle(user, "article-456", "Another Article", "https://example.com/article2");
        List<ReadArticle> readArticles = Arrays.asList(readArticle, readArticle2);
        when(readArticleRepository.findByUserOrderByReadAtDesc(user)).thenReturn(readArticles);

        // When
        List<ReadArticle> result = articleInteractionService.getReadArticles(user);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(readArticle, result.get(0));
        assertEquals(readArticle2, result.get(1));
        verify(readArticleRepository).findByUserOrderByReadAtDesc(user);
    }

    @Test
    void getFavoriteArticles() {
        // Given
        FavoriteArticle favoriteArticle2 = new FavoriteArticle(user, "article-789", "Favorite Article", "https://example.com/article3");
        List<FavoriteArticle> favoriteArticles = Arrays.asList(favoriteArticle, favoriteArticle2);
        when(favoriteArticleRepository.findByUserOrderByFavoritedAtDesc(user)).thenReturn(favoriteArticles);

        // When
        List<FavoriteArticle> result = articleInteractionService.getFavoriteArticles(user);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(favoriteArticle, result.get(0));
        assertEquals(favoriteArticle2, result.get(1));
        verify(favoriteArticleRepository).findByUserOrderByFavoritedAtDesc(user);
    }

    @Test
    void isArticleRead_True() {
        // Given
        when(readArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(true);

        // When
        boolean result = articleInteractionService.isArticleRead(user, articleId);

        // Then
        assertTrue(result);
        verify(readArticleRepository).existsByUserAndArticleId(user, articleId);
    }

    @Test
    void isArticleRead_False() {
        // Given
        when(readArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(false);

        // When
        boolean result = articleInteractionService.isArticleRead(user, articleId);

        // Then
        assertFalse(result);
        verify(readArticleRepository).existsByUserAndArticleId(user, articleId);
    }

    @Test
    void isArticleFavorite_True() {
        // Given
        when(favoriteArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(true);

        // When
        boolean result = articleInteractionService.isArticleFavorite(user, articleId);

        // Then
        assertTrue(result);
        verify(favoriteArticleRepository).existsByUserAndArticleId(user, articleId);
    }

    @Test
    void isArticleFavorite_False() {
        // Given
        when(favoriteArticleRepository.existsByUserAndArticleId(user, articleId)).thenReturn(false);

        // When
        boolean result = articleInteractionService.isArticleFavorite(user, articleId);

        // Then
        assertFalse(result);
        verify(favoriteArticleRepository).existsByUserAndArticleId(user, articleId);
    }
}

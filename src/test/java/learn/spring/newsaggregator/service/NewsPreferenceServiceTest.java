package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.dto.NewsPreferenceDto;
import learn.spring.newsaggregator.entity.NewsPreference;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.repository.NewsPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsPreferenceServiceTest {

    @Mock
    private NewsPreferenceRepository newsPreferenceRepository;

    @InjectMocks
    private NewsPreferenceService newsPreferenceService;

    private User user;
    private NewsPreference newsPreference;
    private NewsPreferenceDto preferenceDto;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        newsPreference = new NewsPreference();
        newsPreference.setUser(user);
        newsPreference.setCategories(Set.of("technology", "business"));
        newsPreference.setSources(Set.of("bbc-news", "cnn"));
        newsPreference.setCountries(Set.of("us"));
        newsPreference.setLanguages(Set.of("en"));

        preferenceDto = new NewsPreferenceDto();
        preferenceDto.setCategories(Set.of("sports", "health"));
        preferenceDto.setSources(Set.of("espn", "reuters"));
        preferenceDto.setCountries(Set.of("uk"));
        preferenceDto.setLanguages(Set.of("en"));
    }

    @Test
    void getPreferenceByUser_ExistingPreference() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.of(newsPreference));

        // When
        NewsPreference result = newsPreferenceService.getPreferenceByUser(user);

        // Then
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(Set.of("technology", "business"), result.getCategories());
        assertEquals(Set.of("bbc-news", "cnn"), result.getSources());
        verify(newsPreferenceRepository).findByUser(user);
    }

    @Test
    void getPreferenceByUser_NoExistingPreference() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.empty());

        // When
        NewsPreference result = newsPreferenceService.getPreferenceByUser(user);

        // Then
        assertNull(result);
        verify(newsPreferenceRepository).findByUser(user);
    }

    @Test
    void updatePreferences_ExistingPreference() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.of(newsPreference));
        when(newsPreferenceRepository.save(any(NewsPreference.class))).thenReturn(newsPreference);

        // When
        NewsPreference result = newsPreferenceService.updatePreferences(user, preferenceDto);

        // Then
        assertNotNull(result);
        assertEquals(Set.of("sports", "health"), newsPreference.getCategories());
        assertEquals(Set.of("espn", "reuters"), newsPreference.getSources());
        assertEquals(Set.of("uk"), newsPreference.getCountries());
        assertEquals(Set.of("en"), newsPreference.getLanguages());
        verify(newsPreferenceRepository).findByUser(user);
        verify(newsPreferenceRepository).save(newsPreference);
    }

    @Test
    void updatePreferences_NewPreference() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(newsPreferenceRepository.save(any(NewsPreference.class))).thenAnswer(invocation -> {
            NewsPreference savedPreference = invocation.getArgument(0);
            savedPreference.setId(1L);
            return savedPreference;
        });

        // When
        NewsPreference result = newsPreferenceService.updatePreferences(user, preferenceDto);

        // Then
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(Set.of("sports", "health"), result.getCategories());
        assertEquals(Set.of("espn", "reuters"), result.getSources());
        assertEquals(Set.of("uk"), result.getCountries());
        assertEquals(Set.of("en"), result.getLanguages());
        verify(newsPreferenceRepository).findByUser(user);
        verify(newsPreferenceRepository).save(any(NewsPreference.class));
    }

    @Test
    void deletePreferences_ExistingPreference() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.of(newsPreference));

        // When
        newsPreferenceService.deletePreferences(user);

        // Then
        verify(newsPreferenceRepository).findByUser(user);
        verify(newsPreferenceRepository).delete(newsPreference);
    }

    @Test
    void deletePreferences_NoExistingPreference() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.empty());

        // When
        newsPreferenceService.deletePreferences(user);

        // Then
        verify(newsPreferenceRepository).findByUser(user);
        verify(newsPreferenceRepository, never()).delete(any(NewsPreference.class));
    }

    @Test
    void updatePreferences_NullCategories() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.of(newsPreference));
        when(newsPreferenceRepository.save(any(NewsPreference.class))).thenReturn(newsPreference);
        
        preferenceDto.setCategories(null);

        // When
        NewsPreference result = newsPreferenceService.updatePreferences(user, preferenceDto);

        // Then
        assertNotNull(result);
        assertNull(newsPreference.getCategories());
        verify(newsPreferenceRepository).save(newsPreference);
    }

    @Test
    void updatePreferences_EmptyCollections() {
        // Given
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.of(newsPreference));
        when(newsPreferenceRepository.save(any(NewsPreference.class))).thenReturn(newsPreference);
        
        preferenceDto.setCategories(Set.of());
        preferenceDto.setSources(Set.of());
        preferenceDto.setCountries(Set.of());
        preferenceDto.setLanguages(Set.of());

        // When
        NewsPreference result = newsPreferenceService.updatePreferences(user, preferenceDto);

        // Then
        assertNotNull(result);
        assertTrue(newsPreference.getCategories().isEmpty());
        assertTrue(newsPreference.getSources().isEmpty());
        assertTrue(newsPreference.getCountries().isEmpty());
        assertTrue(newsPreference.getLanguages().isEmpty());
        verify(newsPreferenceRepository).save(newsPreference);
    }
}

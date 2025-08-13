package learn.spring.newsaggregator.controller;

import learn.spring.newsaggregator.dto.NewsResponseDto;
import learn.spring.newsaggregator.entity.FavoriteArticle;
import learn.spring.newsaggregator.entity.ReadArticle;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.service.ArticleInteractionService;
import learn.spring.newsaggregator.service.AuthService;
import learn.spring.newsaggregator.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ArticleInteractionService articleInteractionService;

    @GetMapping
    public ResponseEntity<NewsResponseDto> getNews() {
        User currentUser = authService.getCurrentUser();
        NewsResponseDto newsResponse = newsService.getNewsForUser(currentUser);
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<NewsResponseDto> searchNews(@PathVariable String keyword,
                                                     @RequestParam(required = false) Set<String> categories,
                                                     @RequestParam(required = false) Set<String> sources) {
        NewsResponseDto newsResponse = newsService.searchNews(keyword, categories, sources);
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping("/sources")
    public ResponseEntity<NewsResponseDto> getAllSources() {
        NewsResponseDto sourcesResponse = newsService.getAllSources();
        return ResponseEntity.ok(sourcesResponse);
    }

    @PostMapping("/{articleId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String articleId,
                                       @RequestParam String title,
                                       @RequestParam String url) {
        try {
            User currentUser = authService.getCurrentUser();
            articleInteractionService.markAsRead(currentUser, articleId, title, url);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Article marked as read successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{articleId}/favorite")
    public ResponseEntity<?> markAsFavorite(@PathVariable String articleId,
                                           @RequestParam String title,
                                           @RequestParam String url) {
        try {
            User currentUser = authService.getCurrentUser();
            articleInteractionService.markAsFavorite(currentUser, articleId, title, url);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Article marked as favorite successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{articleId}/favorite")
    public ResponseEntity<?> removeFavorite(@PathVariable String articleId) {
        try {
            User currentUser = authService.getCurrentUser();
            articleInteractionService.removeFavorite(currentUser, articleId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Article removed from favorites successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/read")
    public ResponseEntity<List<ReadArticle>> getReadArticles() {
        User currentUser = authService.getCurrentUser();
        List<ReadArticle> readArticles = articleInteractionService.getReadArticles(currentUser);
        return ResponseEntity.ok(readArticles);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteArticle>> getFavoriteArticles() {
        User currentUser = authService.getCurrentUser();
        List<FavoriteArticle> favoriteArticles = articleInteractionService.getFavoriteArticles(currentUser);
        return ResponseEntity.ok(favoriteArticles);
    }
}

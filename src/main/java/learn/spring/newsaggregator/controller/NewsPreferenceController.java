package learn.spring.newsaggregator.controller;

import jakarta.validation.Valid;
import learn.spring.newsaggregator.dto.NewsPreferenceDto;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.service.AuthService;
import learn.spring.newsaggregator.service.NewsPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class NewsPreferenceController {

    @Autowired
    private NewsPreferenceService newsPreferenceService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<NewsPreferenceDto> getPreferences() {
        User currentUser = authService.getCurrentUser();
        NewsPreferenceDto preferences = newsPreferenceService.getPreferenceDtoByUser(currentUser);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping
    public ResponseEntity<?> updatePreferences(@Valid @RequestBody NewsPreferenceDto preferenceDto) {
        try {
            User currentUser = authService.getCurrentUser();
            newsPreferenceService.updatePreferences(currentUser, preferenceDto);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Preferences updated successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

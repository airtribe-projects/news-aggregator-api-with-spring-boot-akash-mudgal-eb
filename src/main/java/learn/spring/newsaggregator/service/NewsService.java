package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.dto.NewsResponseDto;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.entity.NewsPreference;
import learn.spring.newsaggregator.exception.NewsApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
public class NewsService {

    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${newsapi.base-url}")
    private String baseUrl;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private NewsPreferenceService newsPreferenceService;

    @Cacheable(value = "news-articles", key = "#user.id + '-' + #categories + '-' + #sources + '-' + #countries")
    public NewsResponseDto getNewsForUser(User user) {
        NewsPreference preference = newsPreferenceService.getPreferenceByUser(user);
        
        if (preference == null) {
            return getTopHeadlines(null, null, null, null);
        }

        return getTopHeadlines(
                preference.getCategories(),
                preference.getSources(),
                preference.getCountries(),
                preference.getLanguages()
        );
    }

    @Cacheable(value = "news-articles", key = "'top-headlines-' + #categories + '-' + #sources + '-' + #countries + '-' + #languages")
    public NewsResponseDto getTopHeadlines(Set<String> categories, Set<String> sources, 
                                          Set<String> countries, Set<String> languages) {
        WebClient webClient = webClientBuilder.build();
        
        String url = buildTopHeadlinesUrl(categories, sources, countries, languages);
        
        try {
            return webClient.get()
                    .uri(url)
                    .header("X-Api-Key", apiKey)
                    .retrieve()
                    .bodyToMono(NewsResponseDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new NewsApiException("Error fetching news from external API: " + e.getMessage());
        }
    }

    @Cacheable(value = "news-articles", key = "'search-' + #keyword + '-' + #categories + '-' + #sources")
    public NewsResponseDto searchNews(String keyword, Set<String> categories, Set<String> sources) {
        WebClient webClient = webClientBuilder.build();
        
        String url = buildSearchUrl(keyword, categories, sources);
        
        try {
            return webClient.get()
                    .uri(url)
                    .header("X-Api-Key", apiKey)
                    .retrieve()
                    .bodyToMono(NewsResponseDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new NewsApiException("Error searching news from external API: " + e.getMessage());
        }
    }

    @Cacheable(value = "news-sources", key = "'all-sources'")
    public NewsResponseDto getAllSources() {
        WebClient webClient = webClientBuilder.build();
        
        String url = baseUrl + "/sources";
        
        try {
            return webClient.get()
                    .uri(url)
                    .header("X-Api-Key", apiKey)
                    .retrieve()
                    .bodyToMono(NewsResponseDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new NewsApiException("Error fetching sources from external API: " + e.getMessage());
        }
    }

    private String buildTopHeadlinesUrl(Set<String> categories, Set<String> sources, 
                                       Set<String> countries, Set<String> languages) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/top-headlines");
        boolean hasParam = false;

        // If sources are specified, don't use country (NewsAPI restriction)
        if (sources != null && !sources.isEmpty()) {
            urlBuilder.append("?sources=").append(String.join(",", sources));
            hasParam = true;
        } else {
            // If no sources, we need at least a country parameter
            if (countries != null && !countries.isEmpty()) {
                urlBuilder.append("?country=").append(String.join(",", countries));
                hasParam = true;
            } else {
                // Default to US if no country specified
                urlBuilder.append("?country=us");
                hasParam = true;
            }
            
            // Add category only if no sources (NewsAPI restriction)
            if (categories != null && !categories.isEmpty()) {
                urlBuilder.append("&category=").append(String.join(",", categories));
            }
        }

        if (languages != null && !languages.isEmpty()) {
            urlBuilder.append(hasParam ? "&" : "?").append("language=").append(String.join(",", languages));
        }

        return urlBuilder.toString();
    }

    private String buildSearchUrl(String keyword, Set<String> categories, Set<String> sources) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/everything");
        
        // URL encode the keyword to handle special characters
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        urlBuilder.append("?q=").append(encodedKeyword);

        // Note: /everything endpoint doesn't support category parameter
        // Categories are handled through keyword search instead
        
        if (sources != null && !sources.isEmpty()) {
            urlBuilder.append("&sources=").append(String.join(",", sources));
        }

        urlBuilder.append("&sortBy=publishedAt");

        return urlBuilder.toString();
    }
}

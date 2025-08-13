package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.dto.NewsResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class NewsUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(NewsUpdateService.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    public void updateCachedNews() {
        logger.info("Starting scheduled news cache update...");
        
        try {
            // Clear existing cache
            if (cacheManager.getCache("news-articles") != null) {
                cacheManager.getCache("news-articles").clear();
            }
            
            // Update popular categories
            String[] popularCategories = {"technology", "business", "science", "health", "sports"};
            String[] popularCountries = {"us", "gb", "ca", "au"};
            
            for (String category : popularCategories) {
                try {
                    Set<String> categories = Set.of(category);
                    NewsResponseDto response = newsService.getTopHeadlines(categories, null, null, null);
                    logger.debug("Updated cache for category: {}, articles count: {}", 
                               category, response.getTotalResults());
                } catch (Exception e) {
                    logger.error("Error updating cache for category {}: {}", category, e.getMessage());
                }
            }

            for (String country : popularCountries) {
                try {
                    Set<String> countries = Set.of(country);
                    NewsResponseDto response = newsService.getTopHeadlines(null, null, countries, null);
                    logger.debug("Updated cache for country: {}, articles count: {}", 
                               country, response.getTotalResults());
                } catch (Exception e) {
                    logger.error("Error updating cache for country {}: {}", country, e.getMessage());
                }
            }
            
            // Update sources
            try {
                newsService.getAllSources();
                logger.debug("Updated sources cache");
            } catch (Exception e) {
                logger.error("Error updating sources cache: {}", e.getMessage());
            }
            
            logger.info("Completed scheduled news cache update");
            
        } catch (Exception e) {
            logger.error("Error during scheduled news cache update: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 600000) // Run every 10 minutes for health check
    public void performHealthCheck() {
        logger.debug("Performing news service health check...");
        
        try {
            NewsResponseDto response = newsService.getTopHeadlines(null, null, Set.of("us"), null);
            logger.debug("Health check successful - API responded with {} articles", 
                       response.getTotalResults());
        } catch (Exception e) {
            logger.warn("Health check failed: {}", e.getMessage());
        }
    }
}

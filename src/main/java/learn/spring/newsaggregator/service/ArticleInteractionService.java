package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.entity.FavoriteArticle;
import learn.spring.newsaggregator.entity.ReadArticle;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.exception.ResourceNotFoundException;
import learn.spring.newsaggregator.repository.FavoriteArticleRepository;
import learn.spring.newsaggregator.repository.ReadArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleInteractionService {

    @Autowired
    private ReadArticleRepository readArticleRepository;

    @Autowired
    private FavoriteArticleRepository favoriteArticleRepository;

    public ReadArticle markAsRead(User user, String articleId, String articleTitle, String articleUrl) {
        if (!readArticleRepository.existsByUserAndArticleId(user, articleId)) {
            ReadArticle readArticle = new ReadArticle(user, articleId, articleTitle, articleUrl);
            return readArticleRepository.save(readArticle);
        }
        return readArticleRepository.findByUserAndArticleId(user, articleId).orElse(null);
    }

    public FavoriteArticle markAsFavorite(User user, String articleId, String articleTitle, String articleUrl) {
        if (!favoriteArticleRepository.existsByUserAndArticleId(user, articleId)) {
            FavoriteArticle favoriteArticle = new FavoriteArticle(user, articleId, articleTitle, articleUrl);
            return favoriteArticleRepository.save(favoriteArticle);
        }
        return favoriteArticleRepository.findByUserAndArticleId(user, articleId).orElse(null);
    }

    public void removeFavorite(User user, String articleId) {
        if (favoriteArticleRepository.existsByUserAndArticleId(user, articleId)) {
            favoriteArticleRepository.deleteByUserAndArticleId(user, articleId);
        } else {
            throw new ResourceNotFoundException("Favorite article not found for user");
        }
    }

    public List<ReadArticle> getReadArticles(User user) {
        return readArticleRepository.findByUserOrderByReadAtDesc(user);
    }

    public List<FavoriteArticle> getFavoriteArticles(User user) {
        return favoriteArticleRepository.findByUserOrderByFavoritedAtDesc(user);
    }

    public boolean isArticleRead(User user, String articleId) {
        return readArticleRepository.existsByUserAndArticleId(user, articleId);
    }

    public boolean isArticleFavorite(User user, String articleId) {
        return favoriteArticleRepository.existsByUserAndArticleId(user, articleId);
    }
}

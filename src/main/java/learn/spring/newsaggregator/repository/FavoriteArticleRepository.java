package learn.spring.newsaggregator.repository;

import learn.spring.newsaggregator.entity.FavoriteArticle;
import learn.spring.newsaggregator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteArticleRepository extends JpaRepository<FavoriteArticle, Long> {

    List<FavoriteArticle> findByUserOrderByFavoritedAtDesc(User user);
    List<FavoriteArticle> findByUserIdOrderByFavoritedAtDesc(Long userId);
    Optional<FavoriteArticle> findByUserAndArticleId(User user, String articleId);
    boolean existsByUserAndArticleId(User user, String articleId);
    void deleteByUserAndArticleId(User user, String articleId);
}

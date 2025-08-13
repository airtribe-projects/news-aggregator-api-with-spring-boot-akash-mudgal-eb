package learn.spring.newsaggregator.repository;

import learn.spring.newsaggregator.entity.ReadArticle;
import learn.spring.newsaggregator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadArticleRepository extends JpaRepository<ReadArticle, Long> {

    List<ReadArticle> findByUserOrderByReadAtDesc(User user);
    List<ReadArticle> findByUserIdOrderByReadAtDesc(Long userId);
    Optional<ReadArticle> findByUserAndArticleId(User user, String articleId);
    boolean existsByUserAndArticleId(User user, String articleId);
}

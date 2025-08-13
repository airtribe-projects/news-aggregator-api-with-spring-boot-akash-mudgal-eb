package learn.spring.newsaggregator.repository;

import learn.spring.newsaggregator.entity.NewsPreference;
import learn.spring.newsaggregator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsPreferenceRepository extends JpaRepository<NewsPreference, Long> {

    Optional<NewsPreference> findByUser(User user);
    Optional<NewsPreference> findByUserId(Long userId);
}

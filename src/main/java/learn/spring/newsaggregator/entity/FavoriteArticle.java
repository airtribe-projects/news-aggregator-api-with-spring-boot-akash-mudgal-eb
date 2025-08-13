package learn.spring.newsaggregator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "favorite_articles")
public class FavoriteArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "article_id", nullable = false)
    private String articleId;

    @Column(name = "article_title")
    private String articleTitle;

    @Column(name = "article_url")
    private String articleUrl;

    @Column(name = "article_description", length = 1000)
    private String articleDescription;

    @Column(name = "article_image_url")
    private String articleImageUrl;

    @Column(name = "favorited_at")
    private LocalDateTime favoritedAt;

    // Constructors
    public FavoriteArticle() {
        this.favoritedAt = LocalDateTime.now();
    }

    public FavoriteArticle(User user, String articleId, String articleTitle, String articleUrl) {
        this();
        this.user = user;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleUrl = articleUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public void setArticleDescription(String articleDescription) {
        this.articleDescription = articleDescription;
    }

    public String getArticleImageUrl() {
        return articleImageUrl;
    }

    public void setArticleImageUrl(String articleImageUrl) {
        this.articleImageUrl = articleImageUrl;
    }

    public LocalDateTime getFavoritedAt() {
        return favoritedAt;
    }

    public void setFavoritedAt(LocalDateTime favoritedAt) {
        this.favoritedAt = favoritedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteArticle that = (FavoriteArticle) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FavoriteArticle{" +
                "id=" + id +
                ", articleId='" + articleId + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", favoritedAt=" + favoritedAt +
                '}';
    }
}

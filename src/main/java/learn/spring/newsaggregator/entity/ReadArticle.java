package learn.spring.newsaggregator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "read_articles")
public class ReadArticle {

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

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // Constructors
    public ReadArticle() {
        this.readAt = LocalDateTime.now();
    }

    public ReadArticle(User user, String articleId, String articleTitle, String articleUrl) {
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

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadArticle that = (ReadArticle) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReadArticle{" +
                "id=" + id +
                ", articleId='" + articleId + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", readAt=" + readAt +
                '}';
    }
}

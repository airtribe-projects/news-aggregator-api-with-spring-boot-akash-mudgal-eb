package learn.spring.newsaggregator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "news_preferences")
public class NewsPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferred_categories", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "category")
    private Set<String> categories;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferred_sources", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "source")
    private Set<String> sources;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferred_countries", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "country")
    private Set<String> countries;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferred_languages", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "language")
    private Set<String> languages;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public NewsPreference() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public NewsPreference(User user) {
        this();
        this.user = user;
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

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public Set<String> getSources() {
        return sources;
    }

    public void setSources(Set<String> sources) {
        this.sources = sources;
    }

    public Set<String> getCountries() {
        return countries;
    }

    public void setCountries(Set<String> countries) {
        this.countries = countries;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsPreference that = (NewsPreference) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NewsPreference{" +
                "id=" + id +
                ", categories=" + categories +
                ", sources=" + sources +
                ", countries=" + countries +
                ", languages=" + languages +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

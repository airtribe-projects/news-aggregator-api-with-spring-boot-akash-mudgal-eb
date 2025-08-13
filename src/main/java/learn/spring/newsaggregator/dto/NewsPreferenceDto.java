package learn.spring.newsaggregator.dto;

import java.util.Set;

public class NewsPreferenceDto {

    private Set<String> categories;
    private Set<String> sources;
    private Set<String> countries;
    private Set<String> languages;

    // Constructors
    public NewsPreferenceDto() {}

    public NewsPreferenceDto(Set<String> categories, Set<String> sources, Set<String> countries, Set<String> languages) {
        this.categories = categories;
        this.sources = sources;
        this.countries = countries;
        this.languages = languages;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "NewsPreferenceDto{" +
                "categories=" + categories +
                ", sources=" + sources +
                ", countries=" + countries +
                ", languages=" + languages +
                '}';
    }
}

package learn.spring.newsaggregator.service;

import learn.spring.newsaggregator.dto.NewsPreferenceDto;
import learn.spring.newsaggregator.entity.NewsPreference;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.repository.NewsPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NewsPreferenceService {

    @Autowired
    private NewsPreferenceRepository newsPreferenceRepository;

    public NewsPreference getPreferenceByUser(User user) {
        return newsPreferenceRepository.findByUser(user).orElse(null);
    }

    public NewsPreference updatePreferences(User user, NewsPreferenceDto preferenceDto) {
        NewsPreference preference = newsPreferenceRepository.findByUser(user)
                .orElse(new NewsPreference(user));

        preference.setCategories(preferenceDto.getCategories());
        preference.setSources(preferenceDto.getSources());
        preference.setCountries(preferenceDto.getCountries());
        preference.setLanguages(preferenceDto.getLanguages());

        return newsPreferenceRepository.save(preference);
    }

    public NewsPreferenceDto getPreferenceDtoByUser(User user) {
        NewsPreference preference = getPreferenceByUser(user);
        
        if (preference == null) {
            return new NewsPreferenceDto();
        }

        return new NewsPreferenceDto(
                preference.getCategories(),
                preference.getSources(),
                preference.getCountries(),
                preference.getLanguages()
        );
    }
}

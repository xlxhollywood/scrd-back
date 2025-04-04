package org.example.scrd.repo;

import org.example.scrd.domain.Theme;

import java.util.List;
import java.util.Optional;

public interface ThemeRepositoryCustom {
    List<Theme> findThemesOrderByReviewCountAndRating();
    List<Theme> filterThemes(
            Integer horror,
            Integer activity,
            Float levelMin,
            Float levelMax,
            Float ratingMin,
            Float ratingMax,
            String location
    );

}

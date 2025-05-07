package org.example.scrd.repo;

import org.example.scrd.domain.Theme;
import org.example.scrd.dto.LocationCountDto;
import org.example.scrd.dto.ThemeDto;

import java.util.List;
import java.util.Optional;

public interface ThemeRepositoryCustom {
    List<Theme> findThemesOrderByReviewCountAndRating();
    List<Theme> findThemesOrderByReviewCountAndRating(int page, int size);

    List<Theme> filterThemes(
            Integer horror,
            Integer activity,
            Float levelMin,
            Float levelMax,
            Float ratingMin,
            Float ratingMax,
            String location
    );

    List<LocationCountDto> countThemesByLocation();

    List<Theme> searchByKeywordAndFilters(String keyword, Integer horror, Integer activity, String location);



}

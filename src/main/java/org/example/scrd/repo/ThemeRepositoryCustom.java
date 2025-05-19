package org.example.scrd.repo;

import org.example.scrd.domain.Theme;
import org.example.scrd.dto.LocationCountDto;
import org.example.scrd.dto.ThemeDto;

import java.util.List;
import java.util.Optional;

public interface ThemeRepositoryCustom {
    List<Theme> findThemesOrderByReviewCountAndRating();
    List<LocationCountDto> countThemesByLocation();

    public List<Theme> findThemesByCriteria(
            String keyword,
            Integer horror,
            Integer activity,
            Float levelMin,
            Float levelMax,
            String location,
            int page,
            int size,
            String sort
    );



}

package org.example.scrd.repo;

import org.example.scrd.domain.Theme;
import org.example.scrd.dto.response.LocationCountResponse;
import java.util.List;


public interface ThemeRepositoryCustom {
    List<Theme> findThemesOrderByReviewCountAndRating();
    List<LocationCountResponse> countThemesByLocation();

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

package org.example.scrd.repo;

import org.example.scrd.domain.Theme;

import java.util.List;

public interface ThemeRepositoryCustom {
    List<Theme> findThemesOrderByReviewCountAndRating();
}

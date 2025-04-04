// ReviewRepositoryCustom.java
package org.example.scrd.repo;

import org.example.scrd.domain.Review;
import java.util.List;

public interface ReviewRepositoryCustom {
    List<Review> searchReviews(String contentKeyword, Long userId, Long themeId);
    float getAverageScoreByThemeId(Long themeId);
    float getAverageHorrorByThemeId(Long themeId);
    float getAverageActivityByThemeId(Long themeId);

    Float getAverageLevelByThemeId(Long themeId);


}

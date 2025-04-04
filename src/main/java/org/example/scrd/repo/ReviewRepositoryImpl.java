// ReviewRepositoryImpl.java
package org.example.scrd.repo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.QReview;
import org.example.scrd.domain.Review;

import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Review> searchReviews(String contentKeyword, Long userId, Long themeId) {
        QReview review = QReview.review;

        return queryFactory
                .selectFrom(review)
                .where(
                        contentKeyword != null ? review.text.containsIgnoreCase(contentKeyword) : null,
                        userId != null ? review.user.id.eq(userId) : null,
                        themeId != null ? review.theme.id.eq(themeId) : null
                )
                .fetch();
    }

    @Override
    public float getAverageScoreByThemeId(Long themeId) {
        QReview review = QReview.review;

        Double avg = queryFactory
                .select(review.stars.avg())
                .from(review)
                .where(review.theme.id.eq(themeId))
                .fetchOne();

        return avg != null ? avg.floatValue() : 0.0f;
    }

    @Override
    public float getAverageHorrorByThemeId(Long themeId) {
        QReview review = QReview.review;

        Double avg = queryFactory
                .select(review.horror.avg())
                .from(review)
                .where(review.theme.id.eq(themeId))
                .fetchOne();

        return avg != null ? avg.floatValue() : 0.0f;
    }

    @Override
    public float getAverageActivityByThemeId(Long themeId) {
        QReview review = QReview.review;

        Double avg = queryFactory
                .select(review.activity.avg())
                .from(review)
                .where(review.theme.id.eq(themeId))
                .fetchOne();

        return avg != null ? avg.floatValue() : 0.0f;
    }

    // ReviewRepositoryImpl.java
    @Override
    public Float getAverageLevelByThemeId(Long themeId) {
        QReview review = QReview.review;

        Double avg = queryFactory
                .select(review.level.avg())
                .from(review)
                .where(review.theme.id.eq(themeId))
                .fetchOne();

        return avg != null ? avg.floatValue() : 0.0f;
    }

}

package org.example.scrd.repo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.QReview;
import org.example.scrd.domain.QTheme;
import org.example.scrd.domain.Theme;

import java.util.List;

@RequiredArgsConstructor
public class ThemeRepositoryCustomImpl implements ThemeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Theme> findThemesOrderByReviewCountAndRating() {
        QTheme theme = QTheme.theme;
        QReview review = QReview.review;

        return queryFactory
                .select(theme)
                .from(review)
                .join(review.theme, theme)
                .groupBy(theme)
                .orderBy(
                        review.count().desc(),
                        review.stars.avg().desc()
                )
                .fetch();
    }



}

package org.example.scrd.repo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.QReview;
import org.example.scrd.domain.QTheme;
import org.example.scrd.domain.Theme;
import org.example.scrd.dto.response.LocationCountResponse;

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

    @Override
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
    ) {
        QTheme theme = QTheme.theme;
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(theme.title.containsIgnoreCase(keyword)
                    .or(theme.brand.containsIgnoreCase(keyword))
                    .or(theme.location.containsIgnoreCase(keyword)));
        }

        if (horror != null) builder.and(theme.horror.eq(horror));
        if (activity != null) builder.and(theme.activity.eq(activity));
        if (levelMin != null) builder.and(theme.level.goe(levelMin));
        if (levelMax != null) builder.and(theme.level.loe(levelMax));
        if (location != null && !location.isEmpty()) builder.and(theme.location.eq(location));

        OrderSpecifier<?>[] orderSpecifiers;
        switch (sort.toLowerCase()) {
            case "rating":
                orderSpecifiers = new OrderSpecifier[]{theme.rating.desc().nullsLast()};
                break;
            case "reviewcount":
                orderSpecifiers = new OrderSpecifier[]{theme.reviewCount.desc().nullsLast()};
                break;
            case "combined":
            default:
                orderSpecifiers = new OrderSpecifier[]{
                        theme.reviewCount.desc().nullsLast(),
                        theme.rating.desc().nullsLast()
                };
                break;
        }

        return queryFactory
                .selectFrom(theme)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public List<LocationCountResponse> countThemesByLocation() {
        QTheme theme = QTheme.theme;

        return queryFactory
                .select(Projections.constructor(
                        LocationCountResponse.class,
                        theme.location,
                        theme.count()
                ))
                .from(theme)
                .groupBy(theme.location)
                .orderBy(theme.count().desc())
                .fetch();
    }

}

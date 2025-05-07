package org.example.scrd.repo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.QReview;
import org.example.scrd.domain.QTheme;
import org.example.scrd.domain.Theme;
import org.example.scrd.dto.LocationCountDto;
import org.example.scrd.dto.ThemeDto;

import java.util.List;
import java.util.Optional;

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
    public List<Theme> findThemesOrderByReviewCountAndRating(int page, int size) {
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
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }



    @Override
    public List<Theme> filterThemes(
            Integer horror,
            Integer activity,
            Float minLevel,
            Float maxLevel,
            Float minRating,
            Float maxRating,
            String location
    ) {
        QTheme theme = QTheme.theme;
        BooleanBuilder builder = new BooleanBuilder();

        if (horror != null) {
            builder.and(theme.horror.eq(horror));
        }
        if (activity != null) {
            builder.and(theme.activity.eq(activity));
        }
        if (minLevel != null) {
            builder.and(theme.level.goe(minLevel));
        }
        if (maxLevel != null) {
            builder.and(theme.level.loe(maxLevel));
        }
        if (minRating != null) {
            builder.and(theme.rating.goe(minRating));
        }
        if (maxRating != null) {
            builder.and(theme.rating.loe(maxRating));
        }
        if (location != null) {
            builder.and(theme.location.eq(location));
        }

        System.out.println("Filter Conditions: " + builder.toString());

        return queryFactory
                .selectFrom(theme)
                .where(builder)
                .orderBy(theme.rating.desc().nullsLast())
                .fetch();
    }


    @Override
    public List<LocationCountDto> countThemesByLocation() {
        QTheme theme = QTheme.theme;

        return queryFactory
                .select(Projections.constructor(
                        LocationCountDto.class,
                        theme.location,
                        theme.count()
                ))
                .from(theme)
                .groupBy(theme.location)
                .orderBy(theme.count().desc())
                .fetch();
    }

    @Override
    public List<Theme> searchByKeywordAndFilters(
            String keyword, Integer horror, Integer activity, String location) {

        QTheme theme = QTheme.theme;
        BooleanBuilder builder = new BooleanBuilder();
        // 키워드를 어느 것으로 쓸 것인가.. 제목 혹은 브랜드 혹은 지역에 있는 키워드를 포함하면 검색됨.
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(theme.title.containsIgnoreCase(keyword)
                    .or(theme.brand.containsIgnoreCase(keyword))
                    .or(theme.location.containsIgnoreCase(keyword)));
        }

        if (horror != null) {
            builder.and(theme.horror.eq(horror));
        }

        if (activity != null) {
            builder.and(theme.activity.eq(activity));
        }

        if (location != null && !location.isEmpty()) {
            builder.and(theme.location.eq(location));
        }

        return queryFactory
                .selectFrom(theme)
                .where(builder)
                .orderBy(theme.rating.desc().nullsLast())
                .fetch();
    }


}

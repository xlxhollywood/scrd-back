package org.example.scrd.repo;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.QPartyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class PartyPostRepositoryImpl implements PartyPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PartyPost> findByConditions(LocalDate deadline, Boolean isClosed, Pageable pageable) {
        QPartyPost post = QPartyPost.partyPost;
        BooleanBuilder builder = new BooleanBuilder();

        if (deadline != null) {
            builder.and(post.deadline.year().eq(deadline.getYear()))
                    .and(post.deadline.month().eq(deadline.getMonthValue()))
                    .and(post.deadline.dayOfMonth().eq(deadline.getDayOfMonth()));
        }

        if (isClosed != null) {
            builder.and(post.isClosed.eq(isClosed));
        }

        List<PartyPost> result = queryFactory
                .selectFrom(post)
                .where(builder)
                .orderBy(post.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory.select(post.count())
                        .from(post)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, pageable, total);
    }
}
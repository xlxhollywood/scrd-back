package org.example.scrd.repo;

import org.example.scrd.domain.Review;
import org.example.scrd.domain.ReviewTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewTagMapRepository extends JpaRepository<ReviewTagMap, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ReviewTagMap rtm WHERE rtm.review = :review")
    void deleteAllByReview(@Param("review") Review review);
}

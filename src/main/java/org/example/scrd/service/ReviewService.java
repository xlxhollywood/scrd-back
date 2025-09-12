package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.*;
import org.example.scrd.dto.response.MyReviewResponse;
import org.example.scrd.dto.ReviewCreateRequestDto;
import org.example.scrd.dto.ThemeReviewResponseDto;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final TagRepository tagRepository;
    private final ReviewTagMapRepository reviewTagMapRepository;

    @Transactional
    public void addReview(ReviewCreateRequestDto dto, Long userId, Theme theme, List<Long> tagIds) {
        // 기존 리뷰 저장 로직 유지
        Review review = Review.addReviewFrom(
                userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다.")),
                dto,
                theme
        );
        reviewRepository.save(review);

        theme.increaseReviewCount();
        themeRepository.save(theme);

        updateThemeRating(review.getTheme().getId());

        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new NotFoundException("해당 태그가 존재하지 않습니다."));
                ReviewTagMap tagMap = ReviewTagMap.builder()
                        .review(review)
                        .tag(tag)
                        .build();
                reviewTagMapRepository.save(tagMap);
            }
        }
    }

    public List<MyReviewResponse> getReviewListByUser(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(MyReviewResponse::from)
                .collect(Collectors.toList());
    }

    public List<ThemeReviewResponseDto> getReviewListByTheme(Long themeId) {
        return reviewRepository.findByThemeId(themeId)
                .stream()
                .map(ThemeReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("해당 기록이 존재하지 않습니다."));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = review.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException();
        }

        // 리뷰-태그 연결 삭제
        reviewTagMapRepository.deleteAllByReview(review);

        // 테마의 리뷰 카운트 감소
        Theme theme = review.getTheme();
        reviewRepository.delete(review);

        theme.decreaseReviewCount();
        themeRepository.save(theme);

        updateThemeRating(theme.getId()); // 삭제 후 평점 갱신
    }

    @Transactional
    public void updateThemeRating(Long themeId) {
        float starsAvg = reviewRepository.getAverageScoreByThemeId(themeId);
        float horrorAvg = reviewRepository.getAverageHorrorByThemeId(themeId);
        float activityAvg = reviewRepository.getAverageActivityByThemeId(themeId);
        float levelAvg = reviewRepository.getAverageLevelByThemeId(themeId);

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("테마 없음"));

        theme.updateRatingAndFlags(starsAvg, levelAvg, horrorAvg, activityAvg);
        themeRepository.save(theme);
    }

    public long countReviewsByUser(Long userId) {
        return reviewRepository.countByUserId(userId);
    }


}

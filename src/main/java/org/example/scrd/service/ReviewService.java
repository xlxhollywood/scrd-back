package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.Review;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.ReviewDto;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.repo.ReviewRepository;
import org.example.scrd.repo.ThemeRepository;
import org.example.scrd.repo.UserRepository;
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


    public void addReview(ReviewDto dto, Long userId, Theme theme) {
        Review review = Review.addReviewFrom(
                userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다.")),
                dto,
                theme
        );

        reviewRepository.save(review);
        // 리뷰에서 테마에 가져오고 테마의 Id를 반환
        updateThemeRating(review.getTheme().getId());
    }


    public List<ReviewDto> getReviewListByUser(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(ReviewDto::from)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewListByTheme(Long themeId) {
        return reviewRepository.findByThemeId(themeId)
                .stream()
                .map(ReviewDto::from)
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

        Long themeId = review.getTheme().getId(); // 삭제 전에 테마 ID 확보
        reviewRepository.delete(review);

        updateThemeRating(themeId); // 삭제 후 평점 갱신
    }


//    public List<Review> searchReviews(String keyword, Long userId, Long themeId) {
//        return reviewRepository.searchReviews(keyword, userId, themeId);
//    }

    @Transactional
    public void updateThemeRating(Long themeId) {
        float avg = reviewRepository.getAverageScoreByThemeId(themeId);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("테마 없음"));
        System.out.println("테마의 평균 avg : " + avg);
        theme.updateRating(avg);
        themeRepository.save(theme);
    }


}

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
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public void addReview(ReviewDto dto, Long userId, Theme theme){
        reviewRepository.save(
                Review.addReviewFrom(
                        userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다.")),
                        dto, theme));
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

    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("해당 기록이 존재하지 않습니다."));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = review.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException();
        }

        reviewRepository.delete(review);
    }

}

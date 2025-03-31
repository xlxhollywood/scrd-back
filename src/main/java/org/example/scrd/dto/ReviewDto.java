package org.example.scrd.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Review;
import org.example.scrd.dto.request.ReviewRequest;

@Builder
@Getter
public class ReviewDto {
    private Long id;
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;

    public static ReviewDto from(ReviewRequest request) {
        return ReviewDto.builder()
                .text(request.getText())
                .level(request.getLevel())
                .stars(request.getStars())
                .horror(request.getHorror())
                .activity(request.getActivity())
                .build();
    }
    public static ReviewDto from(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .text(review.getText())
                .level(review.getLevel())
                .stars(review.getStars())
                .horror(review.getHorror())
                .activity(review.getActivity())
                .build();
    }
}

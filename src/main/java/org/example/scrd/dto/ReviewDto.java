package org.example.scrd.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Review;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.ReviewRequest;
import java.util.List;

@Builder
@Getter
public class ReviewDto {
    private User user;
    private String userTier;
    private String userName;
    private Long id;
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private List<String> tagNames; // 태그 이름 리스트 추가

    // 리뷰 등록용 Dto
    public static ReviewDto from(ReviewRequest request) {
        return ReviewDto.builder()
                .text(request.getText())
                .level(request.getLevel())
                .stars(request.getStars())
                .horror(request.getHorror())
                .activity(request.getActivity())
                .build();
    }

    // 리뷰 가져오기용 Dto
    public static ReviewDto from(Review review) {
        List<String> tagNames = review.getTagMaps().stream()
                .map(tagMap -> tagMap.getTag().getTagName())
                .toList();

        return ReviewDto.builder()
                .userTier(review.getUser().getTier().getTierE()) // <- 여기 수정
                .userName(review.getUser().getName())
                .id(review.getId())
                .text(review.getText())
                .level(review.getLevel())
                .stars(review.getStars())
                .horror(review.getHorror())
                .activity(review.getActivity())
                .tagNames(tagNames)
                .build();
    }
}

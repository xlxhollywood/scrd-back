package org.example.scrd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Review;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ThemeReviewResponse {
    private Long userId;
    private String userTier;
    private String nickName;
    private Long id;
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private LocalDateTime regDate;
    private List<String> tagNames; // 태그 이름 리스트 추가

    private Integer hintUsageCount;
    private Boolean isSuccessful;
    private String clearTime;

// 리뷰 가져오기용 Dto
public static ThemeReviewResponse from(Review review) {
    List<String> tagNames = review.getTagMaps().stream()
            .map(tagMap -> tagMap.getTag().getTagName())
            .toList();

    return ThemeReviewResponse.builder()
            .userId(review.getUser().getId())
            .userTier(review.getUser().getTier().getTierE()) // <- 여기 수정
            .nickName(review.getUser().getNickName())
            .id(review.getId())
            .text(review.getText())
            .level(review.getLevel())
            .stars(review.getStars())
            .horror(review.getHorror())
            .activity(review.getActivity())
            .hintUsageCount(review.getHintUsageCount())
            .isSuccessful(review.getIsSuccessful())
            .clearTime(review.getClearTime())
            .tagNames(tagNames)
            .regDate(review.getRegDate())
            .build();
    }
}
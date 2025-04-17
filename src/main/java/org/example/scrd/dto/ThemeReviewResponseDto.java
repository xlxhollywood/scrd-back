package org.example.scrd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Review;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ThemeReviewResponseDto {
    private String userTier;
    private String nickName;
    private Long id;
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private List<String> tagNames; // 태그 이름 리스트 추가

// 리뷰 가져오기용 Dto
public static ThemeReviewResponseDto from(Review review) {
    List<String> tagNames = review.getTagMaps().stream()
            .map(tagMap -> tagMap.getTag().getTagName())
            .toList();

    return ThemeReviewResponseDto.builder()
            .userTier(review.getUser().getTier().getTierE()) // <- 여기 수정
            .nickName(review.getUser().getNickName())
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
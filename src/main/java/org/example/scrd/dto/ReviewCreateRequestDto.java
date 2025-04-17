package org.example.scrd.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Review;
import org.example.scrd.domain.Theme;
import org.example.scrd.dto.request.ReviewRequest;
import java.util.List;

@Builder
@Getter
public class ReviewCreateRequestDto {

    private String userTier;
    private String nickName;
    private Long id;
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private List<String> tagNames; // 태그 이름 리스트 추가
    private Boolean isSuccessful;
    private Integer hintUsageCount;

    // 리뷰 등록용 Dto
    public static ReviewCreateRequestDto from(ReviewRequest request) {
        return ReviewCreateRequestDto.builder()
                .text(request.getText())
                .level(request.getLevel())
                .stars(request.getStars())
                .horror(request.getHorror())
                .activity(request.getActivity())
                .isSuccessful(request.getIsSuccessful())
                .hintUsageCount(request.getHintUsageCount())
                .build();
    }



}

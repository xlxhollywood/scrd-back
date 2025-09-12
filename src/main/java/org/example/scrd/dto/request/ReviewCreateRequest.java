package org.example.scrd.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReviewCreateRequest {

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
    private String clearTime;

    // 리뷰 등록용 Dto
    public static ReviewCreateRequest from(ReviewRequest request) {
        return ReviewCreateRequest.builder()
                .text(request.getText())
                .level(request.getLevel())
                .stars(request.getStars())
                .horror(request.getHorror())
                .activity(request.getActivity())
                .isSuccessful(request.getIsSuccessful())
                .hintUsageCount(request.getHintUsageCount())
                .clearTime(request.getClearTime())
                .build();
    }



}

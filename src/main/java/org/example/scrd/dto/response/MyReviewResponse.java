package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Review;
import org.example.scrd.domain.Theme;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Getter
public class MyReviewResponse {
    private String userTier;
    private String nickName;
    private Long id;
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private List<String> tagNames; // 태그 이름 리스트 추가
    private Integer hintUsageCount;
    private Boolean isSuccessful;
    private String clearTime;
    private LocalDateTime regDate;

    private String themeTitle;
    private String themeBrand;
    private String themeBranch;
    private String themeLocation;
    private String themeImage;


    public static MyReviewResponse from(Review review) {
        List<String> tagNames = review.getTagMaps().stream()
                .map(tagMap -> tagMap.getTag().getTagName())
                .toList();

        Theme theme = review.getTheme(); // Review -> Theme 연관관계

        return MyReviewResponse.builder()
                .userTier(review.getUser().getTier().getTierE())
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
                .regDate(review.getRegDate())
                .tagNames(tagNames)

                .themeBrand(theme.getBrand())
                .themeTitle(theme.getTitle())
                .themeBranch(theme.getBranch())
                .themeLocation(theme.getLocation())
                .themeImage(theme.getImage())
                .build();
    }
}

package org.example.scrd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private Long themeId;
    private List<Long> tagIds;
    private Boolean isSuccessful;
    private Integer hintUsageCount;
    private String clearTime;
}

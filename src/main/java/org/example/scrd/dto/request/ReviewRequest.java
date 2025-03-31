package org.example.scrd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewRequest {
    private String text;
    private int level;
    private int stars;
    private int horror;
    private int activity;
    private Long themeId;
}

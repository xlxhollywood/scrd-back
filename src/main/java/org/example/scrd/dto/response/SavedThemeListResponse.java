package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavedThemeListResponse {
    private Long themeId;
    private String title;
    private String description;
    private String image;
    private String brand;
    private String branch;
}

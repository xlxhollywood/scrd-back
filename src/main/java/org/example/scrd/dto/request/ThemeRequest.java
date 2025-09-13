package org.example.scrd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeRequest {
    private String title;
    private String description;
    private String location;
    private int price;
    private String image;
    private String url;
    private String brand;
    private String branch;
    private int playtime;
}

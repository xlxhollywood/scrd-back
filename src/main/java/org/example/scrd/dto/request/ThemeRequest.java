package org.example.scrd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
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

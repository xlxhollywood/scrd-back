package org.example.scrd.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Theme;
import org.example.scrd.dto.request.ThemeRequest;

import java.util.List;

@Builder
@Getter
public class ThemeDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Integer price;
    private String image;
    private String url;
    private String brand;
    private String branch;
    private Integer playtime;
    private List<String> themeAvailableTime;

    public static ThemeDto from(ThemeRequest request){
        return ThemeDto.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .price(request.getPrice())
                .image(request.getImage())
                .url(request.getUrl())
                .branch(request.getBranch())
                .brand(request.getBrand())
                .playtime(request.getPlaytime())
                .build();
    }

    public static ThemeDto from(Theme theme) {
        return ThemeDto.builder()
                .id(theme.getId())
                .title(theme.getTitle())
                .description(theme.getDescription())
                .location(theme.getLocation())
                .price(theme.getPrice() != null ? theme.getPrice() : -1)
                .image(theme.getImage())
                .url(theme.getUrl())
                .branch(theme.getBranch())
                .brand(theme.getBrand())
                .playtime(theme.getPlaytime() != null ? theme.getPlaytime() : -1)
                .build();
    }
}

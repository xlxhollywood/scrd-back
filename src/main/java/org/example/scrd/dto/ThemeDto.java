package org.example.scrd.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Theme;
import org.example.scrd.dto.request.ThemeRequest;

import java.io.Serializable;


//@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@AllArgsConstructor
public class ThemeDto implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private Float rating;
    private String proportion; // 테마 비율
    private Integer horror; // 테마 공포도
    private Integer activity; // 테마 활동성
    private Float level; // 테마 난이도
    private Integer reviewCount; // 테마의 리뷰 개수

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

    // 테마를 받아서 Dto 로
    public static ThemeDto toDto(Theme theme) {
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
                .rating(theme.getRating())
                .horror(theme.getHorror())
                .activity(theme.getActivity())
                .level(theme.getLevel())
                .reviewCount(theme.getReviewCount())
                .build();
    }

    public static ThemeDto toWebDto(Theme theme) {
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
                .rating(theme.getRating())
                .horror(theme.getHorror())
                .activity(theme.getActivity())
                .level(theme.getLevel())
                .reviewCount(theme.getReviewCount())
                .proportion(theme.getProportion())
                .horror(theme.getHorror())
                .activity(theme.getActivity())
                .build();
    }
}

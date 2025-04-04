package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.scrd.BaseEntity;
import org.example.scrd.dto.ThemeDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theme extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;
    private Integer price;
    @Column(columnDefinition = "TEXT")
    private String image;
    private String url;
    private String brand; // 카페 이름
    private String branch; // 매장 이름
    private Integer playtime;
    private String proportion; // 장치 비율
    private Float rating; // 테마 평점
    private Integer horror; // 테마 공포도
    private Integer activity; // 테마 활동성
    private Float level;


    // 저장 테마
    //부모 삭제 시 연관된 자식도 함께 삭제 (DB에 반영됨) , 리스트에서 제거된 자식이 실제 DB에서도 삭제됨
    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedTheme> saved = new ArrayList<>();


    public static Theme from(ThemeDto dto){
        return Theme.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .price(dto.getPrice() != null ? dto.getPrice() : -1)
                .image(dto.getImage())
                .url(dto.getUrl())
                .branch(dto.getBranch())
                .brand(dto.getBrand())
                .playtime(dto.getPlaytime() != null ? dto.getPlaytime() : -1)
                .build();
    }

    public void update(ThemeDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.location = dto.getLocation();
        this.price = dto.getPrice() != null ? dto.getPrice() : -1;
        this.image = dto.getImage();
        this.url = dto.getUrl();
        this.branch = dto.getBranch();
        this.brand = dto.getBrand();
        this.playtime = dto.getPlaytime() != null ? dto.getPlaytime() : -1;
    }

    public void updateRatingAndFlags(float avgRating, float level, float horrorAvg, float activityAvg) {
        this.rating = avgRating;
        this.level = level;
        this.horror = horrorAvg >= 0.5 ? 1 : 0;
        this.activity = activityAvg >= 0.5 ? 1 : 0;
    }

}

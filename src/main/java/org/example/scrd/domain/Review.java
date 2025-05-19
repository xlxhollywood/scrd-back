package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.scrd.BaseEntity;
import org.example.scrd.dto.ReviewCreateRequestDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private int level; // 난이도
    private int stars; // 평점
    private int horror;
    private int activity;

    private Boolean isSuccessful;

    private Integer hintUsageCount;

    private String clearTime;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "themeId", nullable = false)
    private Theme theme;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTagMap> tagMaps = new ArrayList<>();


    public static Review addReviewFrom(User user, ReviewCreateRequestDto dto, Theme theme) {
        Review review =
                Review.builder()
                        .user(user.addReviewFrom(user))
                        .text(dto.getText())
                        .level(dto.getLevel())
                        .stars(dto.getStars())
                        .horror(dto.getHorror())
                        .activity(dto.getActivity())
                        .isSuccessful(dto.getIsSuccessful())
                        .hintUsageCount(dto.getHintUsageCount())
                        .clearTime(dto.getClearTime())
                        .theme(theme)
                        .build();
        return review;
    }

}

package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.scrd.BaseEntity;
import org.example.scrd.dto.ReviewDto;

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
    private int level;
    private int stars;
    private int horror;
    private int activity;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "themeId", nullable = false)
    private Theme theme;

    public static Review addReviewFrom(User user, ReviewDto dto, Theme theme) {
        Review review =
                Review.builder()
                        .user(user.addReviewFrom(user))
                        .text(dto.getText())
                        .level(dto.getLevel())
                        .stars(dto.getStars())
                        .horror(dto.getHorror())
                        .activity(dto.getActivity())
                        .theme(theme)
                        .build();
        return review;
    }

}

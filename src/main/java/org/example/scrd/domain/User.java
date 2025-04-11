package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.scrd.BaseEntity;
import org.example.scrd.dto.Tier;
import org.example.scrd.dto.UserDto;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long kakaoId;

    @Setter
    @Column(columnDefinition = "varchar(200)")
    private String name;

    @Setter
    @Column(columnDefinition = "varchar(30)")
    private String email;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Tier tier;
    private String gender;
    private String birth;

    @Setter
    @Column(columnDefinition = "varchar(30)")
    private String nickName;

    @Setter
    private int point; // 결제 시스템 적용 후 사용되는 포인트 review 작성시 +500
    @Setter
    private int count; // review 쓸때마다 +1

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    public void setCount(int count) {
        this.count = count;
        this.tier = Tier.getTierByCount(count);
    }

    @OneToMany(mappedBy = "user")
    private List<SavedTheme> likes = new ArrayList<>();

    // TODO: 추후 성별, 생일 받으면 빌더 타입 수정해야함.
    public static User from(UserDto dto){
        return User.builder()
                .kakaoId(dto.getKakaoId())
                .name(dto.getName())
                .email(dto.getEmail())
                .profileImageUrl(dto.getProfileImageUrl())
                .role(Role.ROLE_USER) // 기본 권한 설정
                .build();
    }
    public static User addReviewFrom(User user) {
        user.setPoint(user.getPoint() + 500); // 포인트 500 증가
        user.setCount(user.getCount() + 1);   // 리뷰 횟수 1 증가
        return user;
    }

}

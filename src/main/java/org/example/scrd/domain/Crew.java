package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.scrd.BaseEntity;
import org.example.scrd.dto.CrewDto;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crew extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String crewName;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING) // 문자열로 저장
    private Role role;

    public static Crew from(User user, CrewDto dto, Role role) {
        return Crew.builder()
                .user(user)
                .crewName(dto.getCrewName())
                .role(role)
                .build();
    }

    public enum Role {
        LEADER, MEMBER
    }
}

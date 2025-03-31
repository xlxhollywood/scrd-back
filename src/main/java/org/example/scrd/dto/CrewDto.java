package org.example.scrd.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Crew;

@Builder
@Getter
public class CrewDto {
    private Long id;
    private String crewName;
    private Crew.Role role;

    public static CrewDto from(Crew crew) {
        return CrewDto.builder()
                .id(crew.getId())
                .crewName(crew.getCrewName())
                .role(crew.getRole())
                .build();
    }
}

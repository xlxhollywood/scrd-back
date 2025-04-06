package org.example.scrd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.scrd.domain.PartyJoin;

@Getter
@AllArgsConstructor
public class PartyJoinDto {
    private Long joinId;
    private Long userId;
    private String username;
    private String status;

    public static PartyJoinDto from(PartyJoin join) {
        return new PartyJoinDto(
                join.getId(),
                join.getUser().getId(),
                join.getUser().getName(), // 필요 시 nickname 등으로 수정
                join.getStatus().name()
        );
    }
}


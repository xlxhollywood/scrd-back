package org.example.scrd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.scrd.domain.PartyJoin;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PartyJoinResponse {
    private Long joinId;
    private Long userId;
    private String username;
    private String status;
    private LocalDateTime regDate;
    private Long postId;
    private String postTitle;

    public static PartyJoinResponse from(PartyJoin join) {
        return new PartyJoinResponse(
                join.getId(),
                join.getUser().getId(),
                join.getUser().getNickName(),
                join.getStatus().name(),
                join.getRegDate(),
                join.getPartyPost().getId(),
                join.getPartyPost().getTitle()
        );
    }
}


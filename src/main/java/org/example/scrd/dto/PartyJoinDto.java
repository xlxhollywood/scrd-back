package org.example.scrd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.scrd.domain.PartyJoin;
import org.example.scrd.domain.PartyPost;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PartyJoinDto {
    private Long joinId;
    private Long userId;
    private String username;
    private String status;
    private LocalDateTime regDate; // ✅ 이걸 생성자에도 포함시켜야 함
    private Long postId;
    private String postTitle;

    public static PartyJoinDto from(PartyJoin join) {
        return new PartyJoinDto(
                join.getId(),
                join.getUser().getId(),
                join.getUser().getNickName(),
                join.getStatus().name(),
                join.getRegDate(),                           // ✅ 누락되었던 regDate 추가
                join.getPartyPost().getId(),
                join.getPartyPost().getTitle()
        );
    }
}


package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.PartyPost;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyPostResponse {

    private Long id;

    private String image;
    private String title; // 파티 모집 제목
    private String themeTitle;
    private String location;
    private Boolean isClosed;
    private LocalDateTime deadline;

    private int currentParticipants;
    private int maxParticipants;

    public static PartyPostResponse from(PartyPost post) {
        return PartyPostResponse.builder()
                .id(post.getId())
                .image(post.getTheme().getImage())
                .title(post.getTitle())
                .themeTitle(post.getTheme().getTitle())
                .location(post.getTheme().getLocation()) // Theme에 location 필드가 있어야 함
                .deadline(post.getDeadline())
                .currentParticipants(post.getCurrentParticipants())
                .maxParticipants(post.getMaxParticipants())
                .isClosed(post.isClosed())
                .build();
    }
}


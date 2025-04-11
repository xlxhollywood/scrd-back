package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.PartyComment;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PartyCommentResponse {
    private Long id;
    private String content;
    private String writerName;
    private Long parentId;
    private LocalDateTime regDate;
    private List<PartyCommentResponse> children;

    public static PartyCommentResponse fromEntity(PartyComment comment) {
        return PartyCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writerName(comment.getWriter().getName())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .regDate(comment.getRegDate())
                .children(comment.getChildren().stream()  // ← 여기서 자식 댓글들을
                        .map(PartyCommentResponse::fromEntity)  // ← 재귀 호출로
                        .toList())  // ← 계속 트리로 구성
                .build();
    }
}


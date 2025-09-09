package org.example.scrd.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyCommentRequest {
    private Long postId;
    private Long parentId;
    private String content;
    private Long writerId;  // 서비스에서 추가

    public static PartyCommentRequest from(PartyCommentRequest request, Long userId) {
        return PartyCommentRequest.builder()
                .postId(request.getPostId())
                .parentId(request.getParentId())
                .content(request.getContent())
                .writerId(userId)
                .build();
    }

    public boolean isReply() {
        return parentId != null;
    }
}


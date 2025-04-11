package org.example.scrd.dto.request;

import lombok.Getter;

@Getter
public class PartyCommentRequest {
    private Long postId;
    private Long parentId; // 대댓글이면 이 값 있음
    private String content;
}


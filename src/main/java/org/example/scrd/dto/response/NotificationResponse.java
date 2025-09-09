package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.Notification;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private String content;
    private boolean isRead;
    private LocalDateTime regDate;

    // 발신자 정보
    private Long senderId;
    private String senderName;
    private String senderProfileImage;

    // 관련 게시글 정보
    private Long relatedPostId;
    private String relatedPostTitle;

    public static NotificationResponse from(Notification notification) {
        NotificationResponseBuilder builder = NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .regDate(notification.getRegDate());

        // 발신자 정보 (있는 경우만)
        if (notification.getSender() != null) {
            builder.senderId(notification.getSender().getId())
                    .senderName(notification.getSender().getNickName())
                    .senderProfileImage(notification.getSender().getProfileImageUrl());
        }

        // 관련 게시글 정보 (있는 경우만)
        if (notification.getRelatedPost() != null) {
            builder.relatedPostId(notification.getRelatedPost().getId())
                    .relatedPostTitle(notification.getRelatedPost().getTitle());
        }

        return builder.build();
    }
}
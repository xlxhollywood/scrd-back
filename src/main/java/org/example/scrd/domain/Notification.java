package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.scrd.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 알림 보낸 사람 (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // 관련 파티 게시글 ID (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_post_id")
    private PartyPost relatedPost;

    // 관련 댓글 ID (새로 추가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_comment_id")
    private PartyComment relatedComment;


    public enum NotificationType {
        JOIN_REQUEST,
        APPROVED,
        REJECTED,
        COMMENT,           // 새로 추가: 댓글 알림
        REPLY             // 새로 추가: 대댓글 알림
    }
}

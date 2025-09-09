package org.example.scrd.repo;

import org.example.scrd.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByRegDateDesc(Long receiverId);

    void deleteByRelatedPostId(Long relatedPostId);
    // 새로 추가: 댓글 관련 알림 삭제
    void deleteByRelatedCommentId(Long relatedCommentId);

    // 읽지 않은 알림 개수 조회
    long countByReceiverIdAndIsReadFalse(Long receiverId);

}

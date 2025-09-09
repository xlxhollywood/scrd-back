package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.Notification;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.NotificationResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.repo.NotificationRepository;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    // ✅ 파티 관련 알림 (기존 notify → partyNotify로 변경)
    public void partyNotify(User receiver, User sender, Notification.NotificationType type, String content, PartyPost relatedPost) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .content(content)
                .relatedPost(relatedPost)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        sseEmitterService.sendNotification(receiver.getId(), content);
    }


    // 댓글 삭제 시 관련 알림 삭제
    @Transactional
    public void deleteNotificationsByComment(Long commentId) {
        notificationRepository.deleteByRelatedCommentId(commentId);
    }


    // 사용자의 모든 알림 조회
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByRegDateDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다."));

        // 본인의 알림인지 확인
        if (!notification.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByRegDateDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    // 읽지 않은 알림 개수 조회
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}

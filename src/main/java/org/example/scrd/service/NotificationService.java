package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.Notification;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.User;
import org.example.scrd.repo.NotificationRepository;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    public void notify(User receiver, User sender, Notification.NotificationType type, String content, PartyPost relatedPost) {
        // DB 저장
        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .content(content)
                .relatedPost(relatedPost)
                .isRead(false)
                .build();
        // 알람 저장
        notificationRepository.save(notification);

        // SSE 실시간 전송
        sseEmitterService.sendNotification(receiver.getId(), content);
    }
}

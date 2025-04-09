package org.example.scrd.repo;

import org.example.scrd.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByRegDateDesc(Long receiverId);

    void deleteByRelatedPostId(Long relatedPostId);

}

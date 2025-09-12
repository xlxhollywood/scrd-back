package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.NotificationResponse;
import org.example.scrd.exception.WrongTokenException;
import org.example.scrd.service.NotificationService;
import org.example.scrd.service.SseEmitterService;
import org.example.scrd.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static javax.crypto.Cipher.SECRET_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scrd/api")
@Tag(name = "Notification", description = "알림 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @Value("${custom.jwt.secret}") // application properties에서 JWT 비밀키를 주입받음
    private String SECRET_KEY;

    @Operation(summary = "실시간 알림 구독", description = "SSE를 통해 실시간 알림을 받기 위해 구독합니다")
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal User user) {
        return sseEmitterService.subscribe(user.getId());
    }

    @Operation(summary = "내 알림 목록 조회", description = "내가 받은 모든 알림 목록을 조회합니다")
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal User user) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다")
    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<ApiResponse<Object>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user) {
        notificationService.markAsRead(notificationId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "내가 받은 모든 알림을 읽음 상태로 변경합니다")
    @PatchMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Object>> markAllAsRead(
            @AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "읽지 않은 알림의 개수를 조회합니다")
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }


}

package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.exception.WrongTokenException;
import org.example.scrd.service.SseEmitterService;
import org.example.scrd.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static javax.crypto.Cipher.SECRET_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scrd/api")
public class NotificationController {

    private final SseEmitterService sseEmitterService;
    private final JwtUtil jwtUtil;

    @Value("${custom.jwt.secret}") // application properties에서 JWT 비밀키를 주입받음
    private String SECRET_KEY;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal User user) {
        return sseEmitterService.subscribe(user.getId());
    }



}

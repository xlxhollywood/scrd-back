package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.UserResponse;
import org.example.scrd.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrd/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal User user) {
        UserResponse userResponse = userService.getUserInfo(user.getId());
        return ResponseEntity.ok(userResponse);
    }


    @GetMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long targetId
    ) {
        userService.deleteUser(currentUser, targetId);
        return ResponseEntity.ok().build();
    }


}

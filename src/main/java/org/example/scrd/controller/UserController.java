package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.PartyJoinRequest;
import org.example.scrd.dto.request.UserProfileUpdateRequest;
import org.example.scrd.dto.response.UserResponse;
import org.example.scrd.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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


    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long targetId
    ) {
        userService.deleteUser(currentUser, targetId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<Object>>updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileUpdateRequest request) {
        userService.updateUserProfile(user, request);
        return ResponseEntity.ok(ApiResponse.success());
    }


}

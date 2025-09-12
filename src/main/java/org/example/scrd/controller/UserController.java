package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.scrd.dto.response.ApiResponse;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.UserProfileUpdateRequest;
import org.example.scrd.dto.response.UserResponse;
import org.example.scrd.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scrd/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping()
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal User user) {
        UserResponse userResponse = userService.getUserInfo(user.getId());
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "사용자 삭제", description = "관리자이거나 본인인 경우 사용자를 삭제합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "삭제할 사용자 ID", required = true) @RequestParam Long targetId
    ) {
        userService.deleteUser(currentUser, targetId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 수정", description = "사용자 프로필 정보를 수정합니다.")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileUpdateRequest request) {
        userService.updateUserProfile(user, request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
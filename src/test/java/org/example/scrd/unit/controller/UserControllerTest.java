package org.example.scrd.controller;

import org.example.scrd.domain.Role;
import org.example.scrd.domain.Tier;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.UserProfileUpdateRequest;
import org.example.scrd.dto.response.ApiResponse;
import org.example.scrd.dto.response.UserResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * UserController 테스트 클래스
 * 
 * 📚 테스트 목적:
 * - 사용자 정보 조회 기능 테스트
 * - 사용자 삭제 기능 테스트 (권한 검증 포함)
 * - 프로필 수정 기능 테스트 (유효성 검증 포함)
 * - JWT 인증이 필요한 API들의 테스트
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    // 테스트에 사용할 샘플 데이터
    private User sampleUser;
    private User adminUser;
    private UserResponse sampleUserResponse;
    private UserProfileUpdateRequest sampleProfileRequest;

    @BeforeEach
    void setUp() {
        // 일반 사용자 생성
        sampleUser = User.builder()
                .id(1L)
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@example.com")
                .nickName("테스트닉네임")
                .profileImageUrl("https://example.com/profile.jpg")
                .tier(Tier.ONE)
                .gender("남성")
                .birth("1990-01-01")
                .point(1000)
                .count(5)
                .role(Role.ROLE_USER)
                .build();

        // 관리자 사용자 생성
        adminUser = User.builder()
                .id(2L)
                .kakaoId(67890L)
                .name("관리자")
                .email("admin@example.com")
                .nickName("관리자닉네임")
                .profileImageUrl("https://example.com/admin.jpg")
                .tier(Tier.FIVE)
                .gender("여성")
                .birth("1985-05-15")
                .point(5000)
                .count(20)
                .role(Role.ROLE_ADMIN)
                .build();

        // 사용자 응답 DTO 생성
        sampleUserResponse = UserResponse.from(sampleUser);

        // 프로필 수정 요청 생성
        sampleProfileRequest = new UserProfileUpdateRequest("새로운닉네임");
    }

    /**
     * 🧪 테스트 1: 사용자 정보 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 인증된 사용자가 자신의 정보를 조회
     * 2. UserService가 사용자 정보를 반환
     * 3. 200 OK와 함께 사용자 정보가 응답됨
     */
    @Test
    @DisplayName("사용자 정보 조회 - 성공")
    void getUserInfo_Success() {
        // Given
        when(userService.getUserInfo(sampleUser.getId())).thenReturn(sampleUserResponse);

        // When
        ResponseEntity<UserResponse> result = userController.getUserInfo(sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("테스트사용자", result.getBody().getName());
        assertEquals("테스트닉네임", result.getBody().getNickName());
        assertEquals("test@example.com", result.getBody().getEmail());
        assertEquals(Tier.ONE, result.getBody().getTier());
        assertEquals(1000, result.getBody().getPoint());

        verify(userService).getUserInfo(sampleUser.getId());
    }

    /**
     * 🧪 테스트 2: 사용자 정보 조회 실패 - 사용자 없음
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 사용자 ID로 조회
     * 2. UserService가 NotFoundException 발생
     * 3. 컨트롤러에서 예외가 전파됨
     */
    @Test
    @DisplayName("사용자 정보 조회 - 사용자 없음")
    void getUserInfo_UserNotFound() {
        // Given
        when(userService.getUserInfo(anyLong()))
                .thenThrow(new NotFoundException("해당 유가 존재하지 않습니다."));

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            userController.getUserInfo(sampleUser);
        });

        verify(userService).getUserInfo(sampleUser.getId());
    }

    /**
     * 🧪 테스트 3: 사용자 삭제 성공 - 본인 삭제
     * 
     * 테스트 시나리오:
     * 1. 사용자가 자신의 계정을 삭제
     * 2. 권한 검증 통과
     * 3. 200 OK 응답
     */
    @Test
    @DisplayName("사용자 삭제 - 본인 삭제 성공")
    void deleteUser_SelfDeletion_Success() {
        // Given
        Long targetId = sampleUser.getId();
        doNothing().when(userService).deleteUser(sampleUser, targetId);

        // When
        ResponseEntity<Void> result = userController.deleteUser(sampleUser, targetId);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).deleteUser(sampleUser, targetId);
    }

    /**
     * 🧪 테스트 4: 사용자 삭제 성공 - 관리자가 다른 사용자 삭제
     * 
     * 테스트 시나리오:
     * 1. 관리자가 다른 사용자의 계정을 삭제
     * 2. 관리자 권한으로 삭제 허용
     * 3. 200 OK 응답
     */
    @Test
    @DisplayName("사용자 삭제 - 관리자가 다른 사용자 삭제 성공")
    void deleteUser_AdminDeletion_Success() {
        // Given
        Long targetId = sampleUser.getId();
        doNothing().when(userService).deleteUser(adminUser, targetId);

        // When
        ResponseEntity<Void> result = userController.deleteUser(adminUser, targetId);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).deleteUser(adminUser, targetId);
    }

    /**
     * 🧪 테스트 5: 사용자 삭제 실패 - 권한 없음
     * 
     * 테스트 시나리오:
     * 1. 일반 사용자가 다른 사용자의 계정을 삭제 시도
     * 2. 권한 검증 실패
     * 3. UnauthorizedAccessException 발생
     */
    @Test
    @DisplayName("사용자 삭제 - 권한 없음")
    void deleteUser_UnauthorizedAccess() {
        // Given
        Long targetId = 999L; // 다른 사용자 ID
        doThrow(new UnauthorizedAccessException())
                .when(userService).deleteUser(sampleUser, targetId);

        // When & Then
        assertThrows(UnauthorizedAccessException.class, () -> {
            userController.deleteUser(sampleUser, targetId);
        });

        verify(userService).deleteUser(sampleUser, targetId);
    }

    /**
     * 🧪 테스트 6: 프로필 수정 성공
     * 
     * 테스트 시나리오:
     * 1. 유효한 닉네임으로 프로필 수정
     * 2. 중복 검사 통과
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("프로필 수정 - 성공")
    void updateProfile_Success() {
        // Given
        doNothing().when(userService).updateUserProfile(sampleUser, sampleProfileRequest);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                userController.updateProfile(sampleUser, sampleProfileRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(userService).updateUserProfile(sampleUser, sampleProfileRequest);
    }

    /**
     * 🧪 테스트 7: 프로필 수정 실패 - 닉네임 중복
     * 
     * 테스트 시나리오:
     * 1. 이미 사용 중인 닉네임으로 수정 시도
     * 2. 중복 검사 실패
     * 3. IllegalArgumentException 발생
     */
    @Test
    @DisplayName("프로필 수정 - 닉네임 중복")
    void updateProfile_DuplicateNickname() {
        // Given
        UserProfileUpdateRequest duplicateRequest = new UserProfileUpdateRequest("기존닉네임");
        doThrow(new IllegalArgumentException("이미 사용 중인 닉네임입니다."))
                .when(userService).updateUserProfile(sampleUser, duplicateRequest);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userController.updateProfile(sampleUser, duplicateRequest);
        });

        verify(userService).updateUserProfile(sampleUser, duplicateRequest);
    }

    /**
     * 🧪 테스트 8: 프로필 수정 실패 - 닉네임 너무 짧음
     * 
     * 테스트 시나리오:
     * 1. 1글자 닉네임으로 수정 시도
     * 2. 유효성 검사 실패
     * 3. IllegalArgumentException 발생
     */
    @Test
    @DisplayName("프로필 수정 - 닉네임 너무 짧음")
    void updateProfile_NicknameTooShort() {
        // Given
        UserProfileUpdateRequest shortNicknameRequest = new UserProfileUpdateRequest("a");
        doThrow(new IllegalArgumentException("닉네임은 최소 2글자 이상이어야 합니다."))
                .when(userService).updateUserProfile(sampleUser, shortNicknameRequest);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userController.updateProfile(sampleUser, shortNicknameRequest);
        });

        verify(userService).updateUserProfile(sampleUser, shortNicknameRequest);
    }

    /**
     * 🧪 테스트 9: 프로필 수정 실패 - 빈 닉네임
     * 
     * 테스트 시나리오:
     * 1. 빈 문자열로 닉네임 수정 시도
     * 2. 유효성 검사 실패
     * 3. IllegalArgumentException 발생
     */
    @Test
    @DisplayName("프로필 수정 - 빈 닉네임")
    void updateProfile_EmptyNickname() {
        // Given
        UserProfileUpdateRequest emptyRequest = new UserProfileUpdateRequest("");
        doThrow(new IllegalArgumentException("닉네임은 비어 있을 수 없습니다."))
                .when(userService).updateUserProfile(sampleUser, emptyRequest);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userController.updateProfile(sampleUser, emptyRequest);
        });

        verify(userService).updateUserProfile(sampleUser, emptyRequest);
    }

    /**
     * 🧪 테스트 10: 프로필 수정 실패 - 사용자 없음
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 사용자로 프로필 수정 시도
     * 2. 사용자 조회 실패
     * 3. NotFoundException 발생
     */
    @Test
    @DisplayName("프로필 수정 - 사용자 없음")
    void updateProfile_UserNotFound() {
        // Given
        User nonExistentUser = User.builder().id(999L).build();
        doThrow(new NotFoundException("해당 유저가 존재하지 않습니다."))
                .when(userService).updateUserProfile(nonExistentUser, sampleProfileRequest);

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            userController.updateProfile(nonExistentUser, sampleProfileRequest);
        });

        verify(userService).updateUserProfile(nonExistentUser, sampleProfileRequest);
    }
}

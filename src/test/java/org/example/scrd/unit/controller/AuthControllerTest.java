package org.example.scrd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.scrd.dto.UserDto;
import org.example.scrd.dto.response.KakaoLoginResponse;
import org.example.scrd.service.AuthService;
import org.example.scrd.service.KakaoService;
import org.example.scrd.service.AppleService;
import org.example.scrd.util.JwtUtil;
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
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthController 테스트 클래스
 * 
 * 📚 테스트 코드 기초 설명:
 * - @ExtendWith(MockitoExtension.class)
@Tag("unit"): Mockito를 사용하겠다는 의미
 * - @Mock: 가짜 객체를 만들어주는 어노테이션 (실제 DB나 외부 API 호출 안함)
 * - @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상 객체
 * - when().thenReturn(): Mock 객체가 특정 상황에서 무엇을 반환할지 설정
 * - verify(): Mock 객체의 메서드가 호출되었는지 확인
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AuthControllerTest {

    // 🎭 Mock 객체들 (가짜 객체)
    @Mock
    private AuthService authService;
    
    @Mock
    private KakaoService kakaoService;
    
    @Mock
    private AppleService appleService;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;

    // 🎯 실제 테스트 대상 객체 (Mock들을 주입받음)
    @InjectMocks
    private AuthController authController;

    // 테스트에 사용할 샘플 데이터
    private UserDto sampleUserDto;
    private List<String> sampleJwtTokens;

    /**
     * 각 테스트 실행 전에 실행되는 메서드
     * 테스트에 필요한 데이터를 미리 준비
     */
    @BeforeEach
    void setUp() {
        // JWT 시크릿 키와 만료 시간을 설정 (실제 값으로 설정)
        ReflectionTestUtils.setField(authController, "SECRET_KEY", "test-secret-key");
        ReflectionTestUtils.setField(authController, "EXPIRE_TIME_MS", 604800000L);
        ReflectionTestUtils.setField(authController, "EXPIRE_REFRESH_TIME_MS", 1814400000L);

        // 샘플 사용자 데이터 생성
        sampleUserDto = UserDto.builder()
                .id(1L)
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .build();

        // 샘플 JWT 토큰 생성
        sampleJwtTokens = Arrays.asList("access-token", "refresh-token");
    }

    /**
     * 🧪 테스트 1: 카카오 로그인 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. 카카오에서 인증 코드를 받음
     * 2. KakaoService가 사용자 정보를 반환
     * 3. AuthService가 로그인 처리
     * 4. JWT 토큰이 생성됨
     * 5. 응답에 사용자 정보가 포함됨
     */
    @Test
    @DisplayName("카카오 로그인 - 성공")
    void kakaoLogin_Success() throws Exception {
        // Given (준비): Mock 객체들이 무엇을 반환할지 설정
        String authCode = "test-auth-code";
        String origin = "http://localhost:3000";
        
        when(request.getHeader("Origin")).thenReturn(origin);
        when(kakaoService.kakaoLogin(anyString(), anyString())).thenReturn(sampleUserDto);
        when(authService.kakaoLogin(any(UserDto.class))).thenReturn(sampleUserDto);
        when(jwtUtil.createToken(anyLong(), anyString(), anyLong(), anyLong()))
                .thenReturn(sampleJwtTokens);

        // When (실행): 실제 테스트할 메서드 호출
        ResponseEntity<KakaoLoginResponse> result = authController.kakaoLogin(
                authCode, request, response);

        // Then (검증): 결과가 예상과 일치하는지 확인
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("테스트사용자", result.getBody().getName());
        assertEquals("test@example.com", result.getBody().getEmail());
        assertEquals("https://example.com/profile.jpg", result.getBody().getProfileImageUrl());

        // Mock 객체들이 제대로 호출되었는지 확인
        verify(kakaoService).kakaoLogin(authCode, origin + "/login/oauth/kakao");
        verify(authService).kakaoLogin(any(UserDto.class));
        verify(jwtUtil).createToken(1L, "test-secret-key", 604800000L, 1814400000L);
    }

    /**
     * 🧪 테스트 2: Apple 로그인 성공 케이스
     */
    @Test
    @DisplayName("Apple 로그인 - 성공")
    void appleLogin_Success() throws Exception {
        // Given
        String code = "test-apple-code";
        String user = "{\"name\":{\"firstName\":\"John\",\"lastName\":\"Doe\"}}";
        
        UserDto appleUserDto = UserDto.builder()
                .id(1L)
                .appleId("test-apple-id")
                .email("apple@example.com")
                .name("John Doe")
                .build();

        // AppleService는 id가 없는 UserDto를 반환
        UserDto appleServiceDto = UserDto.builder()
                .appleId("test-apple-id")
                .email("apple@example.com")
                .name("John Doe")
                .build();
                
        when(appleService.getAppleInfo(anyString())).thenReturn(appleServiceDto);
        when(authService.appleLogin(any(UserDto.class))).thenReturn(appleUserDto);
        when(jwtUtil.createToken(anyLong(), anyString(), anyLong(), anyLong()))
                .thenReturn(sampleJwtTokens);

        // When
        ResponseEntity<org.example.scrd.dto.response.AppleLoginResponse> result = 
                authController.appleLogin(code, null, user, null, request, response);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("John Doe", result.getBody().getName());
        assertEquals("apple@example.com", result.getBody().getEmail());
        assertEquals("test-apple-id", result.getBody().getAppleId());

        verify(appleService).getAppleInfo(code);
        verify(authService).appleLogin(any(UserDto.class));
        verify(jwtUtil).createToken(anyLong(), anyString(), anyLong(), anyLong());
    }

    /**
     * 🧪 테스트 3: Apple 로그인 실패 케이스
     */
    @Test
    @DisplayName("Apple 로그인 - 실패")
    void appleLogin_Failure() throws Exception {
        // Given: AppleService에서 예외 발생하도록 설정
        when(appleService.getAppleInfo(anyString()))
                .thenThrow(new RuntimeException("Apple API 호출 실패"));

        // When & Then: 예외가 발생하면 500 에러 반환
        ResponseEntity<org.example.scrd.dto.response.AppleLoginResponse> result = 
                authController.appleLogin("invalid-code", null, null, null, request, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}

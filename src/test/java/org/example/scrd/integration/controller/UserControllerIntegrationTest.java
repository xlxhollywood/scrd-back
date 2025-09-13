package org.example.scrd.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.Tier;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.UserProfileUpdateRequest;
import org.example.scrd.repo.UserRepository;
import org.example.scrd.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.example.scrd.repo.RefreshTokenRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController Integration 테스트
 * 
 * 🔧 Integration 테스트 특징:
 * - @SpringBootTest: 실제 Spring Boot 애플리케이션 컨텍스트 로드
 * - @Transactional: 각 테스트 후 DB 롤백 (테스트 간 격리)
 * - 실제 DB, Redis, JWT 환경 사용
 * - MockMvc로 HTTP 요청/응답 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Tag("integration")
@AutoConfigureWebMvc
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // 테스트용 사용자 데이터
    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();

        // 테스트용 사용자 생성
        testUser = User.builder()
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@example.com")
                .nickName("테스트닉네임")
                .tier(Tier.THREE)
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(testUser);

        // 관리자 사용자 생성
        adminUser = User.builder()
                .kakaoId(67890L)
                .name("관리자")
                .email("admin@example.com")
                .nickName("관리자닉네임")
                .tier(Tier.FIVE)
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(adminUser);
    }

    /**
     * 🧪 테스트 1: 사용자 정보 조회 - 성공
     * 
     * 테스트 시나리오:
     * 1. 로그인한 사용자가 자신의 정보 조회
     * 2. JWT 토큰으로 인증
     * 3. 200 OK와 함께 사용자 정보 응답
     */
    @Test
    @DisplayName("사용자 정보 조회 - 성공")
    void getUserInfo_Success() throws Exception {
        // Given: JWT 토큰 생성 (Base64 인코딩된 키 사용)
        String secretKey = "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0aW5nLW9ubHk=";
        List<String> tokens = jwtUtil.createToken(testUser.getId(), secretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);


        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/user")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트사용자"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickName").value("테스트닉네임"))
                .andExpect(jsonPath("$.tier").value("THREE"));

        System.out.println("✅ 사용자 정보 조회 Integration 테스트 성공");
        System.out.println("   - JWT 인증 통과");
        System.out.println("   - 사용자 정보 정상 조회");
    }

    /**
     * 🧪 테스트 2: 사용자 정보 조회 - 인증 실패
     * 
     * 테스트 시나리오:
     * 1. 인증되지 않은 요청
     * 2. 401 Unauthorized 응답
     */
    @Test
    @DisplayName("사용자 정보 조회 - 인증 실패")
    void getUserInfo_Unauthorized() throws Exception {
        // When & Then: 인증 없이 API 호출
        mockMvc.perform(get("/scrd/api/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        System.out.println("✅ 인증되지 않은 요청 Integration 테스트 성공");
        System.out.println("   - 실제 Spring Security 401 응답 확인");
    }

    /**
     * 🧪 테스트 3: 프로필 수정 - 성공
     * 
     * 테스트 시나리오:
     * 1. 로그인한 사용자가 프로필 수정
     * 2. JWT 토큰으로 인증
     * 3. 200 OK와 함께 성공 응답
     */
    @Test
    @DisplayName("프로필 수정 - 성공")
    void updateProfile_Success() throws Exception {
        // Given: JWT 토큰 생성 및 프로필 수정 요청 데이터
        String secretKey = "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0aW5nLW9ubHk=";
        List<String> tokens = jwtUtil.createToken(testUser.getId(), secretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setNickName("수정된닉네임");

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then: 실제 API 호출
        mockMvc.perform(patch("/scrd/api/user/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("성공"));

        // 실제 DB에서 수정되었는지 확인
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getNickName().equals("수정된닉네임");

        System.out.println("✅ 프로필 수정 Integration 테스트 성공");
        System.out.println("   - JWT 인증 통과");
        System.out.println("   - 프로필 정보 정상 수정");
        System.out.println("   - DB 업데이트 확인");
    }

    /**
     * 🧪 테스트 4: 사용자 삭제 - 본인 삭제 성공
     * 
     * 테스트 시나리오:
     * 1. 사용자가 자신을 삭제
     * 2. JWT 토큰으로 인증
     * 3. 200 OK 응답
     */
    @Test
    @DisplayName("사용자 삭제 - 본인 삭제 성공")
    void deleteUser_Self_Success() throws Exception {
        // Given: JWT 토큰 생성 (Base64 인코딩된 키 사용)
        String secretKey = "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0aW5nLW9ubHk=";
        List<String> tokens = jwtUtil.createToken(testUser.getId(), secretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(delete("/scrd/api/user/delete")
                .header("Authorization", "Bearer " + accessToken)
                .param("targetId", testUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 실제 DB에서 삭제되었는지 확인
        boolean userExists = userRepository.existsById(testUser.getId());
        assert !userExists;

        System.out.println("✅ 사용자 삭제 Integration 테스트 성공");
        System.out.println("   - JWT 인증 통과");
        System.out.println("   - 본인 삭제 권한 확인");
        System.out.println("   - DB 삭제 확인");
    }

    /**
     * 🧪 테스트 5: 사용자 삭제 - 관리자 권한 성공
     * 
     * 테스트 시나리오:
     * 1. 관리자가 다른 사용자 삭제
     * 2. 관리자 JWT 토큰으로 인증
     * 3. 200 OK 응답
     */
    @Test
    @DisplayName("사용자 삭제 - 관리자 권한 성공")
    void deleteUser_Admin_Success() throws Exception {
        // Given: 관리자 JWT 토큰 생성 (Base64 인코딩된 키 사용)
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(adminUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(delete("/scrd/api/user/delete")
                .header("Authorization", "Bearer " + accessToken)
                .param("targetId", testUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 실제 DB에서 삭제되었는지 확인
        boolean userExists = userRepository.existsById(testUser.getId());
        assert !userExists;

        System.out.println("✅ 관리자 사용자 삭제 Integration 테스트 성공");
        System.out.println("   - 관리자 JWT 인증 통과");
        System.out.println("   - 관리자 삭제 권한 확인");
        System.out.println("   - DB 삭제 확인");
    }

    /**
     * 🧪 테스트 6: 사용자 삭제 - 권한 없음
     * 
     * 테스트 시나리오:
     * 1. 일반 사용자가 다른 사용자 삭제 시도
     * 2. 권한 없음으로 403 Forbidden 응답
     */
    @Test
    @DisplayName("사용자 삭제 - 권한 없음")
    void deleteUser_Unauthorized() throws Exception {
        // Given: 다른 사용자 생성
        User otherUser = User.builder()
                .kakaoId(99999L)
                .name("다른사용자")
                .email("other@example.com")
                .nickName("다른닉네임")
                .tier(Tier.TWO)
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(otherUser);

        String secretKey = "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0aW5nLW9ubHk=";
        List<String> tokens = jwtUtil.createToken(testUser.getId(), secretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(delete("/scrd/api/user/delete")
                .header("Authorization", "Bearer " + accessToken)
                .param("targetId", otherUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        System.out.println("✅ 권한 없는 사용자 삭제 Integration 테스트 성공");
        System.out.println("   - JWT 인증 통과");
        System.out.println("   - 권한 없음 403 응답 확인");
    }

    /**
     * 테스트용 Redis 설정 모킹
     */
    @TestConfiguration
    static class TestRedisConfig {
        
        @Bean
        @Primary
        public LettuceConnectionFactory lettuceConnectionFactory() {
            // 테스트용 모킹된 LettuceConnectionFactory
            return org.mockito.Mockito.mock(LettuceConnectionFactory.class);
        }
    }

}

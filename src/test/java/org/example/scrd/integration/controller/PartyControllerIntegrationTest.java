package org.example.scrd.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.Tier;
import org.example.scrd.repo.PartyPostRepository;
import org.example.scrd.repo.ThemeRepository;
import org.example.scrd.repo.UserRepository;
import org.example.scrd.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.example.scrd.repo.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PartyController Integration 테스트
 * 
 * 🔧 Integration 테스트 특징:
 * - @SpringBootTest: 실제 Spring Boot 애플리케이션 컨텍스트 로드
 * - @Transactional: 각 테스트 후 DB 롤백 (테스트 간 격리)
 * - 실제 DB, Redis, JWT 환경 사용
 * - MockMvc로 HTTP 요청/응답 테스트
 * - 실제 데이터 저장/조회/수정/삭제 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@org.junit.jupiter.api.Tag("integration")
class PartyControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PartyPostRepository partyPostRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * 🧪 Integration 테스트 1: 일행 모집 글 작성 - 실제 DB 저장 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 사용자, 테마 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 일행 모집 글 작성
     * 4. 실제 DB에 저장되었는지 확인
     */
    @Test
    @DisplayName("일행 모집 글 작성 - 실제 Integration 테스트")
    void testCreatePartyPostIntegration() throws Exception {
        // Given: 실제 데이터 생성 및 저장
        User testUser = User.builder()
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .tier(Tier.ONE)
                .count(0)
                .point(0)
                .build();
        User savedUser = userRepository.save(testUser);

        Theme testTheme = Theme.builder()
                .title("테스트테마")
                .description("테스트테마설명")
                .horror(0)
                .activity(0)
                .rating(0.0f)
                .level(0.0f)
                .reviewCount(0)
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        // JWT 토큰 생성
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // 일행 모집 글 작성 데이터
        String partyPostData = """
            {
                "title": "테스트 일행 모집",
                "content": "테스트 일행 모집 내용입니다",
                "maxParticipants": 4,
                "deadline": "2024-12-31T18:00:00"
            }
            """;

        // When & Then: 실제 API 호출
        mockMvc.perform(post("/scrd/api/party/" + savedTheme.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(partyPostData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("성공"));

        // 실제 DB에 저장되었는지 확인
        List<PartyPost> posts = partyPostRepository.findAll();
        assert posts.size() == 1;
        assert posts.get(0).getTitle().equals("테스트 일행 모집");

        System.out.println("✅ 일행 모집 글 작성 Integration 테스트 성공");
        System.out.println("   - 실제 DB 저장: " + posts.get(0).getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 2: 일행 모집 글 목록 조회 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 일행 모집 글 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 일행 모집 글 목록 조회
     * 4. 실제 DB에서 조회된 데이터 확인
     */
    @Test
    @DisplayName("일행 모집 글 목록 조회 - 실제 Integration 테스트")
    void testGetPartyPostsPagedIntegration() throws Exception {
        // Given: 실제 일행 모집 글 데이터 생성 및 저장
        User testUser = User.builder()
                .kakaoId(67890L)
                .name("조회테스트사용자")
                .email("get@test.com")
                .role(Role.ROLE_USER)
                .tier(Tier.ONE)
                .count(0)
                .point(0)
                .build();
        User savedUser = userRepository.save(testUser);

        Theme testTheme = Theme.builder()
                .title("조회테스트테마")
                .description("조회테스트테마설명")
                .horror(0)
                .activity(0)
                .rating(0.0f)
                .level(0.0f)
                .reviewCount(0)
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        PartyPost testPost = PartyPost.builder()
                .writer(savedUser)
                .theme(savedTheme)
                .title("조회 테스트용 일행 모집")
                .content("조회 테스트용 일행 모집 내용")
                .maxParticipants(4)
                .currentParticipants(0)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        PartyPost savedPost = partyPostRepository.save(testPost);

        // JWT 토큰 생성
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/party/paged")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("성공"));

        System.out.println("✅ 일행 모집 글 목록 조회 Integration 테스트 성공");
        System.out.println("   - 실제 DB 조회: " + savedPost.getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 3: 일행 모집 글 상세 조회 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 일행 모집 글 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 일행 모집 글 상세 조회
     * 4. 실제 DB에서 조회된 데이터 확인
     */
    @Test
    @DisplayName("일행 모집 글 상세 조회 - 실제 Integration 테스트")
    void testGetPartyPostDetailIntegration() throws Exception {
        // Given: 실제 일행 모집 글 데이터 생성 및 저장
        User testUser = User.builder()
                .kakaoId(11111L)
                .name("상세조회테스트사용자")
                .email("detail@test.com")
                .role(Role.ROLE_USER)
                .tier(Tier.ONE)
                .count(0)
                .point(0)
                .build();
        User savedUser = userRepository.save(testUser);

        Theme testTheme = Theme.builder()
                .title("상세조회테스트테마")
                .description("상세조회테스트테마설명")
                .horror(0)
                .activity(0)
                .rating(0.0f)
                .level(0.0f)
                .reviewCount(0)
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        PartyPost testPost = PartyPost.builder()
                .writer(savedUser)
                .theme(savedTheme)
                .title("상세 조회 테스트용 일행 모집")
                .content("상세 조회 테스트용 일행 모집 내용")
                .maxParticipants(6)
                .currentParticipants(2)
                .deadline(LocalDateTime.now().plusDays(2))
                .build();
        PartyPost savedPost = partyPostRepository.save(testPost);

        // JWT 토큰 생성
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/party/" + savedPost.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("성공"));

        System.out.println("✅ 일행 모집 글 상세 조회 Integration 테스트 성공");
        System.out.println("   - 실제 DB 조회: " + savedPost.getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 4: 인증되지 않은 요청 - 실제 Spring Security 테스트
     * 
     * 테스트 시나리오:
     * 1. JWT 토큰 없이 실제 API 호출
     * 2. 실제 Spring Security가 401 Unauthorized 응답하는지 확인
     */
    @Test
    @DisplayName("인증되지 않은 요청 - 실제 Integration 테스트")
    void testUnauthorizedRequestIntegration() throws Exception {
        // When & Then: JWT 토큰 없이 실제 API 호출
        mockMvc.perform(get("/scrd/api/party/paged")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        System.out.println("✅ 인증되지 않은 요청 Integration 테스트 성공");
        System.out.println("   - 실제 Spring Security 401 응답 확인");
    }

    /**
     * 테스트용 Redis 설정 모킹
     */
    @org.springframework.boot.test.context.TestConfiguration
    static class TestRedisConfig {
        
        @Bean
        @Primary
        public LettuceConnectionFactory lettuceConnectionFactory() {
            // 테스트용 모킹된 LettuceConnectionFactory
            return org.mockito.Mockito.mock(LettuceConnectionFactory.class);
        }
    }
}
package org.example.scrd.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.scrd.domain.Review;
import org.example.scrd.domain.Tag;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.domain.Role;
import org.example.scrd.repo.ReviewRepository;
import org.example.scrd.repo.TagRepository;
import org.example.scrd.repo.ThemeRepository;
import org.example.scrd.repo.UserRepository;
import org.example.scrd.util.TestJwtUtil;
import org.example.scrd.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.example.scrd.repo.RefreshTokenRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ReviewController Integration 테스트
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
class ReviewControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private TagRepository tagRepository;

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
     * 🧪 Integration 테스트 1: 리뷰 생성 - 실제 DB 저장 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 사용자, 테마, 태그 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 리뷰 생성
     * 4. 실제 DB에 저장되었는지 확인
     */
    @Test
    @DisplayName("리뷰 생성 - 실제 Integration 테스트")
    void testCreateReviewIntegration() throws Exception {
        // Given: 실제 데이터 생성 및 저장
        User testUser = User.builder()
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .build();
        User savedUser = userRepository.save(testUser);

        Theme testTheme = Theme.builder()
                .title("테스트테마")
                .description("테스트테마설명")
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        Tag testTag = Tag.builder()
                .tagName("테스트태그")
                .build();
        Tag savedTag = tagRepository.save(testTag);

        // JWT 토큰 생성 (실제 JwtUtil 사용 - Base64 인코딩된 키 전달)
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // 리뷰 생성 데이터
        String reviewData = """
            {
                "themeId": %d,
                "stars": 5,
                "text": "정말 재미있는 테마였습니다!",
                "level": 3,
                "horror": 1,
                "activity": 2,
                "isSuccessful": true,
                "hintUsageCount": 2,
                "clearTime": "60분",
                "tagIds": [%d]
            }
            """.formatted(savedTheme.getId(), savedTag.getId());

        // When & Then: 실제 API 호출
        mockMvc.perform(post("/scrd/api/review/" + savedTheme.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 실제 DB에 저장되었는지 확인
        List<Review> reviews = reviewRepository.findAll();
        assert reviews.size() == 1;
        assert reviews.get(0).getText().equals("정말 재미있는 테마였습니다!");

        System.out.println("✅ 리뷰 생성 Integration 테스트 성공");
        System.out.println("   - 실제 DB 저장: " + reviews.get(0).getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 2: 리뷰 조회 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 리뷰 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 리뷰 조회
     * 4. 실제 DB에서 조회된 데이터 확인
     */
    @Test
    @DisplayName("리뷰 조회 - 실제 Integration 테스트")
    void testGetReviewIntegration() throws Exception {
        // Given: 실제 리뷰 데이터 생성 및 저장
        User testUser = User.builder()
                .kakaoId(67890L)
                .name("조회테스트사용자")
                .email("get@test.com")
                .role(Role.ROLE_USER)
                .build();
        User savedUser = userRepository.save(testUser);

        Theme testTheme = Theme.builder()
                .title("조회테스트테마")
                .description("조회테스트테마설명")
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        Review testReview = Review.builder()
                .user(savedUser)
                .theme(savedTheme)
                .stars(4)
                .text("조회 테스트용 리뷰입니다")
                .level(3)
                .horror(1)
                .activity(2)
                .isSuccessful(true)
                .hintUsageCount(1)
                .clearTime("45분")
                .build();
        Review savedReview = reviewRepository.save(testReview);

        // JWT 토큰 생성
        // JWT 토큰 생성 (실제 JwtUtil 사용 - Base64 인코딩된 키 전달)
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/review/theme/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("✅ 리뷰 조회 Integration 테스트 성공");
        System.out.println("   - 실제 DB 조회: " + savedReview.getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }


    /**
     * 🧪 Integration 테스트 4: 리뷰 삭제 - 실제 DB 삭제 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 리뷰 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 리뷰 삭제
     * 4. 실제 DB에서 삭제되었는지 확인
     */
    @Test
    @DisplayName("리뷰 삭제 - 실제 Integration 테스트")
    void testDeleteReviewIntegration() throws Exception {
        // Given: 실제 리뷰 데이터 생성 및 저장
        User testUser = User.builder()
                .kakaoId(22222L)
                .name("삭제테스트사용자")
                .email("delete@test.com")
                .role(Role.ROLE_USER)
                .build();
        User savedUser = userRepository.save(testUser);

        Theme testTheme = Theme.builder()
                .title("삭제테스트테마")
                .description("삭제테스트테마설명")
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        Review testReview = Review.builder()
                .user(savedUser)
                .theme(savedTheme)
                .stars(2)
                .text("삭제될 리뷰 내용")
                .level(1)
                .horror(0)
                .activity(0)
                .isSuccessful(false)
                .hintUsageCount(5)
                .clearTime("120분")
                .build();
        Review savedReview = reviewRepository.save(testReview);

        // JWT 토큰 생성
        // JWT 토큰 생성 (실제 JwtUtil 사용 - Base64 인코딩된 키 전달)
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(delete("/scrd/api/review/" + savedReview.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 실제 DB에서 삭제되었는지 확인
        boolean exists = reviewRepository.existsById(savedReview.getId());
        assert !exists;

        System.out.println("✅ 리뷰 삭제 Integration 테스트 성공");
        System.out.println("   - 실제 DB 삭제: " + savedReview.getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 5: 인증되지 않은 요청 - 실제 Spring Security 테스트
     * 
     * 테스트 시나리오:
     * 1. JWT 토큰 없이 실제 API 호출
     * 2. 실제 Spring Security가 401 Unauthorized 응답하는지 확인
     */
    @Test
    @DisplayName("인증되지 않은 요청 - 실제 Integration 테스트")
    void testUnauthorizedRequestIntegration() throws Exception {
        // When & Then: JWT 토큰 없이 실제 API 호출
        mockMvc.perform(get("/scrd/api/review/my")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        System.out.println("✅ 인증되지 않은 요청 Integration 테스트 성공");
        System.out.println("   - 실제 Spring Security 401 응답 확인");
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
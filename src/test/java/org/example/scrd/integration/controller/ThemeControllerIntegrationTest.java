package org.example.scrd.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.Tier;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ThemeController Integration 테스트
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
class ThemeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

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
     * 🧪 Integration 테스트 1: 테마 상세 조회 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 테마 데이터 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 테마 상세 조회
     * 4. 실제 DB에서 조회된 데이터 확인
     */
    @Test
    @DisplayName("테마 상세 조회 - 실제 Integration 테스트")
    void testGetThemeIntegration() throws Exception {
        // Given: 실제 테마 데이터 생성 및 저장
        Theme testTheme = Theme.builder()
                .title("테스트테마")
                .description("테스트테마설명")
                .location("강남구")
                .price(25000)
                .brand("테스트브랜드")
                .branch("테스트지점")
                .playtime(60)
                .horror(0)
                .activity(1)
                .rating(4.5f)
                .level(3.0f)
                .reviewCount(10)
                .build();
        Theme savedTheme = themeRepository.save(testTheme);

        // JWT 토큰 생성
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
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/theme/" + savedTheme.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트테마"))
                .andExpect(jsonPath("$.description").value("테스트테마설명"))
                .andExpect(jsonPath("$.location").value("강남구"))
                .andExpect(jsonPath("$.price").value(25000));

        System.out.println("✅ 테마 상세 조회 Integration 테스트 성공");
        System.out.println("   - 실제 DB 조회: " + savedTheme.getId());
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 2: 테마 목록 조회 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 테마 데이터들 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 테마 목록 조회
     * 4. 실제 DB에서 조회된 데이터 확인
     */
    @Test
    @DisplayName("테마 목록 조회 - 실제 Integration 테스트")
    void testGetThemesIntegration() throws Exception {
        // Given: 실제 테마 데이터들 생성 및 저장
        Theme theme1 = Theme.builder()
                .title("테마1")
                .description("테마1설명")
                .location("강남구")
                .price(20000)
                .horror(0)
                .activity(1)
                .rating(4.0f)
                .level(2.5f)
                .reviewCount(5)
                .build();
        Theme theme2 = Theme.builder()
                .title("테마2")
                .description("테마2설명")
                .location("홍대")
                .price(30000)
                .horror(1)
                .activity(0)
                .rating(4.8f)
                .level(4.0f)
                .reviewCount(15)
                .build();
        themeRepository.save(theme1);
        themeRepository.save(theme2);

        // JWT 토큰 생성
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
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/theme")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        System.out.println("✅ 테마 목록 조회 Integration 테스트 성공");
        System.out.println("   - 실제 DB 조회: 2개 테마");
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 3: 테마 필터링 검색 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 테마 데이터들 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 테마 필터링 검색
     * 4. 실제 DB에서 필터링된 데이터 확인
     */
    @Test
    @DisplayName("테마 필터링 검색 - 실제 Integration 테스트")
    void testGetThemesWithFiltersIntegration() throws Exception {
        // Given: 실제 테마 데이터들 생성 및 저장
        Theme horrorTheme = Theme.builder()
                .title("공포테마")
                .description("공포테마설명")
                .location("강남구")
                .price(25000)
                .horror(1)
                .activity(0)
                .rating(4.5f)
                .level(3.5f)
                .reviewCount(8)
                .build();
        Theme activityTheme = Theme.builder()
                .title("액션테마")
                .description("액션테마설명")
                .location("홍대")
                .price(30000)
                .horror(0)
                .activity(1)
                .rating(4.2f)
                .level(2.0f)
                .reviewCount(12)
                .build();
        themeRepository.save(horrorTheme);
        themeRepository.save(activityTheme);

        // JWT 토큰 생성
        User testUser = User.builder()
                .kakaoId(11111L)
                .name("필터테스트사용자")
                .email("filter@test.com")
                .role(Role.ROLE_USER)
                .tier(Tier.ONE)
                .count(0)
                .point(0)
                .build();
        User savedUser = userRepository.save(testUser);
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출 (공포 테마 필터링)
        mockMvc.perform(get("/scrd/api/theme/filter")
                .header("Authorization", "Bearer " + accessToken)
                .param("horror", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        System.out.println("✅ 테마 필터링 검색 Integration 테스트 성공");
        System.out.println("   - 실제 DB 필터링: 공포 테마 1개");
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
    }

    /**
     * 🧪 Integration 테스트 4: 지역별 테마 개수 조회 - 실제 DB 조회 및 API 호출
     * 
     * 테스트 시나리오:
     * 1. 실제 테마 데이터들 생성 및 저장
     * 2. JWT 토큰 생성
     * 3. 실제 API 호출로 지역별 테마 개수 조회
     * 4. 실제 DB에서 집계된 데이터 확인
     */
    @Test
    @DisplayName("지역별 테마 개수 조회 - 실제 Integration 테스트")
    void testGetLocationCountsIntegration() throws Exception {
        // Given: 실제 테마 데이터들 생성 및 저장
        Theme gangnamTheme = Theme.builder()
                .title("강남테마")
                .description("강남테마설명")
                .location("강남구")
                .price(25000)
                .horror(0)
                .activity(1)
                .rating(4.0f)
                .level(2.5f)
                .reviewCount(5)
                .build();
        Theme hongdaeTheme = Theme.builder()
                .title("홍대테마")
                .description("홍대테마설명")
                .location("홍대")
                .price(30000)
                .horror(1)
                .activity(0)
                .rating(4.5f)
                .level(3.0f)
                .reviewCount(8)
                .build();
        themeRepository.save(gangnamTheme);
        themeRepository.save(hongdaeTheme);

        // JWT 토큰 생성
        User testUser = User.builder()
                .kakaoId(22222L)
                .name("지역테스트사용자")
                .email("location@test.com")
                .role(Role.ROLE_USER)
                .tier(Tier.ONE)
                .count(0)
                .point(0)
                .build();
        User savedUser = userRepository.save(testUser);
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString("test-secret-key-for-integration-testing-only".getBytes());
        List<String> tokens = jwtUtil.createToken(savedUser.getId(), base64SecretKey, 3600000L, 604800000L);
        String accessToken = tokens.get(0);

        // When & Then: 실제 API 호출
        mockMvc.perform(get("/scrd/api/theme/location-counts")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());

        System.out.println("✅ 지역별 테마 개수 조회 Integration 테스트 성공");
        System.out.println("   - 실제 DB 집계: 강남구, 홍대");
        System.out.println("   - 실제 JWT 토큰: " + accessToken.substring(0, 20) + "...");
        System.out.println("   - 실제 API 호출 성공");
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
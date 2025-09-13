package org.example.scrd.controller;

import org.example.scrd.domain.Theme;
import org.example.scrd.dto.request.ThemeRequest;
import org.example.scrd.dto.response.MobileThemeResponse;
import org.example.scrd.dto.response.ThemeAvailableTimeResponse;
import org.example.scrd.dto.response.ThemeResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.service.ThemeService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ThemeController 테스트 클래스
 * 
 * 📚 테스트 목적:
 * - 테마 등록/수정 기능 테스트 (관리자 권한)
 * - 테마 조회 기능 테스트 (상세, 웹용)
 * - 테마 목록 조회 기능 테스트 (정렬 포함)
 * - 테마 필터링 검색 기능 테스트
 * - 예약 가능 시간 조회 기능 테스트
 * - 지역별 테마 개수 조회 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ThemeControllerTest {

    @Mock
    private ThemeService themeService;

    @InjectMocks
    private ThemeController themeController;

    // 테스트에 사용할 샘플 데이터
    private Theme sampleTheme;
    private ThemeRequest sampleThemeRequest;
    private ThemeResponse sampleThemeResponse;
    private MobileThemeResponse sampleMobileThemeResponse;
    private ThemeAvailableTimeResponse sampleAvailableTimeResponse;

    @BeforeEach
    void setUp() {
        // 테마 엔티티 생성
        sampleTheme = Theme.builder()
                .id(1L)
                .title("테스트 테마")
                .description("정말 재미있는 방탈출 테마입니다")
                .location("서울시 강남구")
                .price(25000)
                .image("https://example.com/theme.jpg")
                .url("https://example.com/theme")
                .brand("테스트 브랜드")
                .branch("강남점")
                .playtime(60)
                .build();

        // 테마 요청 DTO 생성 (Builder 패턴 사용)
        sampleThemeRequest = ThemeRequest.builder()
                .title("테스트 테마")
                .description("정말 재미있는 방탈출 테마입니다")
                .location("서울시 강남구")
                .price(25000)
                .image("https://example.com/theme.jpg")
                .url("https://example.com/theme")
                .brand("테스트 브랜드")
                .branch("강남점")
                .playtime(60)
                .build();

        // 테마 응답 DTO 생성
        sampleThemeResponse = ThemeResponse.builder()
                .id(1L)
                .title("테스트 테마")
                .description("정말 재미있는 방탈출 테마입니다")
                .location("서울시 강남구")
                .price(25000)
                .image("https://example.com/theme.jpg")
                .url("https://example.com/theme")
                .brand("테스트 브랜드")
                .branch("강남점")
                .playtime(60)
                .build();

        // 모바일 테마 응답 DTO 생성
        sampleMobileThemeResponse = MobileThemeResponse.mobileBuilder()
                .theme(sampleTheme)
                .availableTimes(Arrays.asList("10:00", "12:00", "14:00"))
                .build();

        // 예약 가능 시간 응답 DTO 생성
        sampleAvailableTimeResponse = new ThemeAvailableTimeResponse(
                "2024-01-15",
                Arrays.asList("10:00", "12:00", "14:00", "16:00", "18:00")
        );
    }

    /**
     * 🧪 테스트 1: 테마 등록 성공
     * 
     * 테스트 시나리오:
     * 1. 관리자가 새로운 테마 등록
     * 2. ThemeService가 테마를 저장
     * 3. 200 OK 응답
     */
    @Test
    @DisplayName("테마 등록 - 성공")
    void addTheme_Success() {
        // Given
        doNothing().when(themeService).addTheme(any(ThemeResponse.class));

        // When
        ResponseEntity<Void> result = themeController.addTheme(sampleThemeRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getBody());

        verify(themeService).addTheme(any(ThemeResponse.class));
    }

    /**
     * 🧪 테스트 2: 테마 수정 성공
     * 
     * 테스트 시나리오:
     * 1. 관리자가 기존 테마 수정
     * 2. ThemeService가 테마를 업데이트
     * 3. 200 OK 응답
     */
    @Test
    @DisplayName("테마 수정 - 성공")
    void updateTheme_Success() {
        // Given
        Long themeId = 1L;
        doNothing().when(themeService).updateTheme(anyLong(), any(ThemeResponse.class));

        // When
        ResponseEntity<Void> result = themeController.updateTheme(themeId, sampleThemeRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getBody());

        verify(themeService).updateTheme(anyLong(), any(ThemeResponse.class));
    }

    /**
     * 🧪 테스트 3: 테마 수정 실패 - 테마 없음
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 테마 ID로 수정 시도
     * 2. ThemeService가 NotFoundException 발생
     * 3. 예외가 전파됨
     */
    @Test
    @DisplayName("테마 수정 - 테마 없음")
    void updateTheme_ThemeNotFound() {
        // Given
        Long themeId = 999L;
        doThrow(new NotFoundException("방탈출 주제가 존재하지 않습니다."))
                .when(themeService).updateTheme(anyLong(), any(ThemeResponse.class));

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            themeController.updateTheme(themeId, sampleThemeRequest);
        });

        verify(themeService).updateTheme(anyLong(), any(ThemeResponse.class));
    }

    /**
     * 🧪 테스트 4: 테마 상세 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 특정 테마의 상세 정보 조회
     * 2. ThemeService가 테마를 반환
     * 3. 200 OK와 함께 테마 정보 응답
     */
    @Test
    @DisplayName("테마 상세 조회 - 성공")
    void getTheme_Success() {
        // Given
        Long themeId = 1L;
        when(themeService.getThemeById(themeId)).thenReturn(sampleTheme);

        // When
        ResponseEntity<ThemeResponse> result = themeController.getTheme(themeId);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("테스트 테마", result.getBody().getTitle());
        assertEquals("서울시 강남구", result.getBody().getLocation());
        assertEquals(25000, result.getBody().getPrice());

        verify(themeService).getThemeById(themeId);
    }

    /**
     * 🧪 테스트 5: 테마 상세 조회 실패 - 테마 없음
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 테마 ID로 조회 시도
     * 2. ThemeService가 RuntimeException 발생
     * 3. 예외가 전파됨
     */
    @Test
    @DisplayName("테마 상세 조회 - 테마 없음")
    void getTheme_ThemeNotFound() {
        // Given
        Long themeId = 999L;
        when(themeService.getThemeById(themeId))
                .thenThrow(new RuntimeException("해당 테마가 없습니다."));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            themeController.getTheme(themeId);
        });

        verify(themeService).getThemeById(themeId);
    }

    /**
     * 🧪 테스트 6: 웹용 테마 상세 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 웹 전용 테마 상세 정보 조회
     * 2. ThemeService가 테마를 반환
     * 3. 200 OK와 함께 웹용 테마 정보 응답
     */
    @Test
    @DisplayName("웹용 테마 상세 조회 - 성공")
    void getThemeDetail_Success() {
        // Given
        Long themeId = 1L;
        when(themeService.getThemeById(themeId)).thenReturn(sampleTheme);

        // When
        ResponseEntity<ThemeResponse> result = themeController.getThemeDetail(themeId);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("테스트 테마", result.getBody().getTitle());

        verify(themeService).getThemeById(themeId);
    }

    /**
     * 🧪 테스트 7: 테마 목록 조회 성공 (기본 정렬)
     * 
     * 테스트 시나리오:
     * 1. 모든 테마 목록 조회 (정렬 없음)
     * 2. ThemeService가 테마 목록을 반환
     * 3. 200 OK와 함께 테마 목록 응답
     */
    @Test
    @DisplayName("테마 목록 조회 - 기본 정렬 성공")
    void getThemes_DefaultSort_Success() {
        // Given
        List<ThemeResponse> expectedThemes = Arrays.asList(sampleThemeResponse);
        when(themeService.getAllThemes()).thenReturn(expectedThemes);

        // When
        ResponseEntity<List<ThemeResponse>> result = themeController.getThemes(null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("테스트 테마", result.getBody().get(0).getTitle());

        verify(themeService).getAllThemes();
        verify(themeService, never()).getThemesSortedByRating();
    }

    /**
     * 🧪 테스트 8: 테마 목록 조회 성공 (평점 정렬)
     * 
     * 테스트 시나리오:
     * 1. 평점순으로 정렬된 테마 목록 조회
     * 2. ThemeService가 정렬된 테마 목록을 반환
     * 3. 200 OK와 함께 정렬된 테마 목록 응답
     */
    @Test
    @DisplayName("테마 목록 조회 - 평점 정렬 성공")
    void getThemes_RatingSort_Success() {
        // Given
        List<ThemeResponse> expectedThemes = Arrays.asList(sampleThemeResponse);
        when(themeService.getThemesSortedByRating()).thenReturn(expectedThemes);

        // When
        ResponseEntity<List<ThemeResponse>> result = themeController.getThemes("rating");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("테스트 테마", result.getBody().get(0).getTitle());

        verify(themeService).getThemesSortedByRating();
        verify(themeService, never()).getAllThemes();
    }

    /**
     * 🧪 테스트 9: 테마 필터링 검색 성공
     * 
     * 테스트 시나리오:
     * 1. 다양한 조건으로 테마 필터링 검색
     * 2. ThemeService가 필터링된 결과를 반환
     * 3. 200 OK와 함께 필터링된 테마 목록 응답
     */
    @Test
    @DisplayName("테마 필터링 검색 - 성공")
    void getThemesWithFiltersAndSorting_Success() {
        // Given
        String keyword = "테스트";
        Integer horror = 2;
        Integer activity = 3;
        Float levelMin = 1.0f;
        Float levelMax = 5.0f;
        String location = "서울시 강남구";
        LocalDate date = LocalDate.of(2024, 1, 15);
        int page = 0;
        int size = 20;
        String sort = "combined";

        List<MobileThemeResponse> expectedResults = Arrays.asList(sampleMobileThemeResponse);
        when(themeService.getThemesByFilterCriteria(
                keyword, horror, activity, levelMin, levelMax, location, date, page, size, sort
        )).thenReturn(expectedResults);

        // When
        ResponseEntity<List<MobileThemeResponse>> result = themeController.getThemesWithFiltersAndSorting(
                keyword, horror, activity, levelMin, levelMax, location, date, page, size, sort
        );

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("테스트 테마", result.getBody().get(0).getTitle());

        verify(themeService).getThemesByFilterCriteria(
                keyword, horror, activity, levelMin, levelMax, location, date, page, size, sort
        );
    }

    /**
     * 🧪 테스트 10: 테마 예약 가능 시간 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 특정 날짜의 테마 예약 가능 시간대 조회
     * 2. ThemeService가 예약 가능 시간을 반환
     * 3. 200 OK와 함께 예약 가능 시간 응답
     */
    @Test
    @DisplayName("테마 예약 가능 시간 조회 - 성공")
    void getAvailableTimesByDate_Success() {
        // Given
        Long themeId = 1L;
        String date = "2024-01-15";
        List<String> availableTimes = Arrays.asList("10:00", "12:00", "14:00", "16:00", "18:00");
        when(themeService.getAvailableTimesByDate(themeId, date)).thenReturn(availableTimes);

        // When
        ResponseEntity<ThemeAvailableTimeResponse> result = 
                themeController.getAvailableTimesByDate(themeId, date);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(date, result.getBody().getDateOfThemeAvailableTime());
        assertEquals(5, result.getBody().getAvailableTime().size());
        assertTrue(result.getBody().getAvailableTime().contains("10:00"));

        verify(themeService).getAvailableTimesByDate(themeId, date);
    }

    /**
     * 🧪 테스트 11: 지역별 테마 개수 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 각 지역별 테마 개수와 전체 개수 조회
     * 2. ThemeService가 지역별 통계를 반환
     * 3. 200 OK와 함께 통계 정보 응답
     */
    @Test
    @DisplayName("지역별 테마 개수 조회 - 성공")
    void getLocationCounts_Success() {
        // Given
        Map<String, Object> expectedCounts = new HashMap<>();
        expectedCounts.put("서울시 강남구", 5);
        expectedCounts.put("서울시 서초구", 3);
        expectedCounts.put("total", 8);
        
        when(themeService.getLocationCountsWithTotal()).thenReturn(expectedCounts);

        // When
        ResponseEntity<Map<String, Object>> result = themeController.getLocationCounts();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(3, result.getBody().size());
        assertEquals(5, result.getBody().get("서울시 강남구"));
        assertEquals(8, result.getBody().get("total"));

        verify(themeService).getLocationCountsWithTotal();
    }

    /**
     * 🧪 테스트 12: 빈 테마 목록 조회
     * 
     * 테스트 시나리오:
     * 1. 테마가 없는 경우 목록 조회
     * 2. 빈 리스트 반환
     * 3. 200 OK와 빈 리스트 응답
     */
    @Test
    @DisplayName("빈 테마 목록 조회 - 성공")
    void getThemes_EmptyList() {
        // Given
        when(themeService.getAllThemes()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<ThemeResponse>> result = themeController.getThemes(null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());

        verify(themeService).getAllThemes();
    }
}

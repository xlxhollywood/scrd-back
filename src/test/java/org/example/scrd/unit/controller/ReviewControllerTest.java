package org.example.scrd.controller;

import org.example.scrd.domain.*;
import org.example.scrd.dto.request.ReviewRequest;
import org.example.scrd.dto.response.ApiResponse;
import org.example.scrd.dto.response.MyReviewResponse;
import org.example.scrd.dto.response.ThemeReviewResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.service.ReviewService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReviewController 테스트 클래스
 * 
 * 📚 테스트 목적:
 * - 리뷰 등록 기능 테스트
 * - 내가 작성한 리뷰 조회 기능 테스트
 * - 테마별 리뷰 조회 기능 테스트
 * - 리뷰 삭제 기능 테스트 (권한 검증 포함)
 * - 리뷰 개수 조회 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;
    
    @Mock
    private ThemeService themeService;

    @InjectMocks
    private ReviewController reviewController;

    // 테스트에 사용할 샘플 데이터
    private User sampleUser;
    private Theme sampleTheme;
    private Review sampleReview;
    private ReviewRequest sampleReviewRequest;
    private MyReviewResponse sampleMyReviewResponse;
    private ThemeReviewResponse sampleThemeReviewResponse;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        sampleUser = User.builder()
                .id(1L)
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@example.com")
                .nickName("테스트닉네임")
                .tier(Tier.THREE)
                .role(Role.ROLE_USER)
                .build();

        // 테마 생성
        sampleTheme = Theme.builder()
                .id(1L)
                .title("테스트 테마")
                .brand("테스트 브랜드")
                .branch("테스트 지점")
                .location("서울시 강남구")
                .image("https://example.com/theme.jpg")
                .build();

        // 리뷰 생성
        sampleReview = Review.builder()
                .id(1L)
                .text("정말 재미있는 테마였습니다!")
                .level(3)
                .stars(5)
                .horror(2)
                .activity(4)
                .hintUsageCount(2)
                .isSuccessful(true)
                .clearTime("60분")
                .user(sampleUser)
                .theme(sampleTheme)
                .build();

        // 리뷰 요청 DTO 생성 (Builder 패턴 사용)
        sampleReviewRequest = ReviewRequest.builder()
                .text("정말 재미있는 테마였습니다!")
                .level(3)
                .stars(5)
                .horror(2)
                .activity(4)
                .themeId(1L)
                .tagIds(Arrays.asList(1L, 2L))
                .isSuccessful(true)
                .hintUsageCount(2)
                .clearTime("60분")
                .build();

        // 내 리뷰 응답 DTO 생성
        sampleMyReviewResponse = MyReviewResponse.builder()
                .userTier("THREE")
                .nickName("테스트닉네임")
                .id(1L)
                .text("정말 재미있는 테마였습니다!")
                .level(3)
                .stars(5)
                .horror(2)
                .activity(4)
                .tagNames(Arrays.asList("공포", "액션"))
                .hintUsageCount(2)
                .isSuccessful(true)
                .clearTime("60분")
                .regDate(LocalDateTime.now())
                .themeTitle("테스트 테마")
                .themeBrand("테스트 브랜드")
                .themeBranch("테스트 지점")
                .themeLocation("서울시 강남구")
                .themeImage("https://example.com/theme.jpg")
                .build();

        // 테마 리뷰 응답 DTO 생성
        sampleThemeReviewResponse = ThemeReviewResponse.builder()
                .id(1L)
                .text("정말 재미있는 테마였습니다!")
                .level(3)
                .stars(5)
                .horror(2)
                .activity(4)
                .nickName("테스트닉네임")
                .userTier("THREE")
                .regDate(LocalDateTime.now())
                .build();
    }

    /**
     * 🧪 테스트 1: 리뷰 등록 성공
     * 
     * 테스트 시나리오:
     * 1. 유효한 리뷰 데이터로 등록 요청
     * 2. ThemeService가 테마를 반환
     * 3. ReviewService가 리뷰를 저장
     * 4. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("리뷰 등록 - 성공")
    void addReview_Success() {
        // Given
        Long themeId = 1L;
        when(themeService.getThemeById(themeId)).thenReturn(sampleTheme);
        doNothing().when(reviewService).addReview(any(), anyLong(), any(Theme.class), anyList());

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                reviewController.addReview(themeId, sampleReviewRequest, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(themeService).getThemeById(themeId);
        verify(reviewService).addReview(any(), eq(sampleUser.getId()), eq(sampleTheme), anyList());
    }

    /**
     * 🧪 테스트 2: 리뷰 등록 실패 - 테마 없음
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 테마 ID로 리뷰 등록 시도
     * 2. ThemeService가 NotFoundException 발생
     * 3. 예외가 전파됨
     */
    @Test
    @DisplayName("리뷰 등록 - 테마 없음")
    void addReview_ThemeNotFound() {
        // Given
        Long themeId = 999L;
        when(themeService.getThemeById(themeId))
                .thenThrow(new NotFoundException("테마를 찾을 수 없습니다."));

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            reviewController.addReview(themeId, sampleReviewRequest, sampleUser);
        });

        verify(themeService).getThemeById(themeId);
        verify(reviewService, never()).addReview(any(), anyLong(), any(Theme.class), anyList());
    }

    /**
     * 🧪 테스트 3: 내가 작성한 리뷰 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 로그인한 사용자가 자신의 리뷰 조회
     * 2. ReviewService가 리뷰 목록을 반환
     * 3. 200 OK와 함께 리뷰 목록 응답
     */
    @Test
    @DisplayName("내가 작성한 리뷰 조회 - 성공")
    void getReviewsByUser_Success() {
        // Given
        List<MyReviewResponse> expectedReviews = Arrays.asList(sampleMyReviewResponse);
        when(reviewService.getReviewListByUser(sampleUser.getId())).thenReturn(expectedReviews);

        // When
        ResponseEntity<List<MyReviewResponse>> result = 
                reviewController.getReviewsByUser(sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("테스트닉네임", result.getBody().get(0).getNickName());
        assertEquals("정말 재미있는 테마였습니다!", result.getBody().get(0).getText());

        verify(reviewService).getReviewListByUser(sampleUser.getId());
    }

    /**
     * 🧪 테스트 4: 테마별 리뷰 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 특정 테마의 리뷰 조회
     * 2. ReviewService가 해당 테마의 리뷰 목록을 반환
     * 3. 200 OK와 함께 리뷰 목록 응답
     */
    @Test
    @DisplayName("테마별 리뷰 조회 - 성공")
    void getReviewsByTheme_Success() {
        // Given
        Long themeId = 1L;
        List<ThemeReviewResponse> expectedReviews = Arrays.asList(sampleThemeReviewResponse);
        when(reviewService.getReviewListByTheme(themeId)).thenReturn(expectedReviews);

        // When
        ResponseEntity<List<ThemeReviewResponse>> result = 
                reviewController.getReviewsByTheme(themeId);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("테스트닉네임", result.getBody().get(0).getNickName());
        assertEquals("정말 재미있는 테마였습니다!", result.getBody().get(0).getText());

        verify(reviewService).getReviewListByTheme(themeId);
    }

    /**
     * 🧪 테스트 5: 리뷰 삭제 성공 - 본인 리뷰
     * 
     * 테스트 시나리오:
     * 1. 사용자가 자신이 작성한 리뷰 삭제
     * 2. 권한 검증 통과
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("리뷰 삭제 - 본인 리뷰 성공")
    void deleteReview_SelfReview_Success() {
        // Given
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(reviewId, sampleUser);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                reviewController.deleteReview(reviewId, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(reviewService).deleteReview(reviewId, sampleUser);
    }

    /**
     * 🧪 테스트 6: 리뷰 삭제 성공 - 관리자 권한
     * 
     * 테스트 시나리오:
     * 1. 관리자가 다른 사용자의 리뷰 삭제
     * 2. 관리자 권한으로 삭제 허용
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("리뷰 삭제 - 관리자 권한 성공")
    void deleteReview_AdminAccess_Success() {
        // Given
        User adminUser = User.builder()
                .id(2L)
                .role(Role.ROLE_ADMIN)
                .build();
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(reviewId, adminUser);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                reviewController.deleteReview(reviewId, adminUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(reviewService).deleteReview(reviewId, adminUser);
    }

    /**
     * 🧪 테스트 7: 리뷰 삭제 실패 - 권한 없음
     * 
     * 테스트 시나리오:
     * 1. 일반 사용자가 다른 사용자의 리뷰 삭제 시도
     * 2. 권한 검증 실패
     * 3. UnauthorizedAccessException 발생
     */
    @Test
    @DisplayName("리뷰 삭제 - 권한 없음")
    void deleteReview_UnauthorizedAccess() {
        // Given
        Long reviewId = 1L;
        doThrow(new UnauthorizedAccessException())
                .when(reviewService).deleteReview(reviewId, sampleUser);

        // When & Then
        assertThrows(UnauthorizedAccessException.class, () -> {
            reviewController.deleteReview(reviewId, sampleUser);
        });

        verify(reviewService).deleteReview(reviewId, sampleUser);
    }

    /**
     * 🧪 테스트 8: 리뷰 삭제 실패 - 리뷰 없음
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 리뷰 ID로 삭제 시도
     * 2. NotFoundException 발생
     * 3. 예외가 전파됨
     */
    @Test
    @DisplayName("리뷰 삭제 - 리뷰 없음")
    void deleteReview_ReviewNotFound() {
        // Given
        Long reviewId = 999L;
        doThrow(new NotFoundException("해당 기록이 존재하지 않습니다."))
                .when(reviewService).deleteReview(reviewId, sampleUser);

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            reviewController.deleteReview(reviewId, sampleUser);
        });

        verify(reviewService).deleteReview(reviewId, sampleUser);
    }

    /**
     * 🧪 테스트 9: 리뷰 개수 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 로그인한 사용자의 리뷰 개수 조회
     * 2. ReviewService가 리뷰 개수를 반환
     * 3. 200 OK와 함께 리뷰 개수 응답
     */
    @Test
    @DisplayName("리뷰 개수 조회 - 성공")
    void countReviewsByUser_Success() {
        // Given
        long expectedCount = 5L;
        when(reviewService.countReviewsByUser(sampleUser.getId())).thenReturn(expectedCount);

        // When
        ResponseEntity<ApiResponse<Long>> result = 
                reviewController.countReviewsByAuthenticatedUser(sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertEquals(expectedCount, result.getBody().getData());

        verify(reviewService).countReviewsByUser(sampleUser.getId());
    }

    /**
     * 🧪 테스트 10: 빈 리뷰 목록 조회
     * 
     * 테스트 시나리오:
     * 1. 리뷰를 작성하지 않은 사용자의 리뷰 조회
     * 2. 빈 리스트 반환
     * 3. 200 OK와 빈 리스트 응답
     */
    @Test
    @DisplayName("빈 리뷰 목록 조회 - 성공")
    void getReviewsByUser_EmptyList() {
        // Given
        when(reviewService.getReviewListByUser(sampleUser.getId())).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<MyReviewResponse>> result = 
                reviewController.getReviewsByUser(sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());

        verify(reviewService).getReviewListByUser(sampleUser.getId());
    }
}

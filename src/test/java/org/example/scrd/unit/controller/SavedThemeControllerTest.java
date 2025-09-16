package org.example.scrd.unit.controller;

import org.example.scrd.controller.SavedThemeController;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.Tier;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.MobileThemeResponse;
import org.example.scrd.dto.response.SavedThemeResponse;
import org.example.scrd.service.SavedThemeService;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SavedThemeController 테스트 클래스
 *
 * 📚 테스트 목적:
 * - 찜한 테마 목록 조회 기능 테스트
 * - 테마 찜하기/취소 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class SavedThemeControllerTest {

    @Mock
    private SavedThemeService savedThemeService;

    @InjectMocks
    private SavedThemeController savedThemeController;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        sampleUser = User.builder()
                .id(1L)
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@example.com")
                .nickName("테스트닉네임")
                .role(Role.ROLE_USER)
                .tier(Tier.ONE)
                .build();
    }

    /**
     * 🧪 테스트 1: 찜한 테마 목록 조회 - 성공 (날짜 지정)
     *
     * 테스트 시나리오:
     * 1. 특정 날짜를 지정하여 찜한 테마 목록 조회
     * 2. SavedThemeService가 MobileThemeResponse 리스트를 반환
     * 3. 200 OK와 함께 해당 리스트가 응답됨
     */
    @Test
    @DisplayName("찜한 테마 목록 조회 - 성공 (날짜 지정)")
    void getSavedThemesWithTimes_WithDate_Success() {
        // Given
        LocalDate specificDate = LocalDate.of(2025, 9, 15);
        MobileThemeResponse themeResponse = new MobileThemeResponse(); // 필드 설정 생략
        List<MobileThemeResponse> responseList = Collections.singletonList(themeResponse);

        when(savedThemeService.getSavedThemesWithAvailableTimes(sampleUser.getId(), specificDate))
                .thenReturn(responseList);

        // When
        ResponseEntity<List<MobileThemeResponse>> result = savedThemeController.getSavedThemesWithTimes(sampleUser, specificDate);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(savedThemeService).getSavedThemesWithAvailableTimes(sampleUser.getId(), specificDate);
    }

    /**
     * 🧪 테스트 2: 찜한 테마 목록 조회 - 성공 (날짜 미지정)
     *
     * 테스트 시나리오:
     * 1. 날짜를 지정하지 않고 찜한 테마 목록 조회 (오늘 날짜로 처리)
     * 2. SavedThemeService가 MobileThemeResponse 리스트를 반환
     * 3. 200 OK와 함께 해당 리스트가 응답됨
     */
    @Test
    @DisplayName("찜한 테마 목록 조회 - 성공 (날짜 미지정, 오늘 날짜)")
    void getSavedThemesWithTimes_WithoutDate_Success() {
        // Given
        LocalDate today = LocalDate.now();
        List<MobileThemeResponse> responseList = Collections.emptyList();

        when(savedThemeService.getSavedThemesWithAvailableTimes(sampleUser.getId(), today))
                .thenReturn(responseList);

        // When
        ResponseEntity<List<MobileThemeResponse>> result = savedThemeController.getSavedThemesWithTimes(sampleUser, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().size());
        verify(savedThemeService).getSavedThemesWithAvailableTimes(sampleUser.getId(), today);
    }

    /**
     * 🧪 테스트 3: 테마 찜하기/취소 - 성공
     *
     * 테스트 시나리오:
     * 1. 특정 테마에 대해 찜하기/취소 요청
     * 2. SavedThemeService가 토글 로직을 처리하고 결과를 SavedThemeResponse로 반환
     * 3. 200 OK와 함께 결과 응답
     */
    @Test
    @DisplayName("테마 찜하기/취소 - 성공")
    void saveUserTheme_Toggle_Success() {
        // Given
        Long themeId = 10L;
        SavedThemeResponse savedResponse = new SavedThemeResponse(true); // 찜하기 성공을 가정

        when(savedThemeService.savedUserTheme(themeId, sampleUser.getId()))
                .thenReturn(savedResponse);

        // When
        ResponseEntity<SavedThemeResponse> result = savedThemeController.saveUserTheme(themeId, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().isSaved());
        verify(savedThemeService).savedUserTheme(themeId, sampleUser.getId());
    }
}

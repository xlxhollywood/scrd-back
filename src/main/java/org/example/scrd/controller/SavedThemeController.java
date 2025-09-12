package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.dto.MobileThemeDto;
import org.example.scrd.dto.response.SavedThemeListResponse;
import org.example.scrd.dto.response.SavedThemeResponse;
import org.example.scrd.service.SavedThemeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/scrd/api/save")
@RequiredArgsConstructor
@Tag(name = "SavedTheme", description = "찜한 테마 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class SavedThemeController {
    private final SavedThemeService savedThemeService;

    @Operation(summary = "찜한 테마 목록 조회", description = "내가 찜한 테마 목록을 예약 가능 시간과 함께 조회합니다")
    @GetMapping
    public ResponseEntity<List<MobileThemeDto>> getSavedThemesWithTimes(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<MobileThemeDto> savedThemes = savedThemeService.getSavedThemesWithAvailableTimes(user.getId(), targetDate);
        return ResponseEntity.ok(savedThemes);
    }

    @Operation(summary = "테마 찜하기/취소", description = "테마를 찜하거나 찜을 취소합니다 (토글 방식)")
    @PostMapping("/{themeId}")
    public ResponseEntity<SavedThemeResponse> saveUserTheme(
            @PathVariable Long themeId,
            @AuthenticationPrincipal User user) {
        SavedThemeResponse savedThemeResponse = savedThemeService.savedUserTheme(themeId, user.getId());
        return ResponseEntity.ok(savedThemeResponse);
    }
}

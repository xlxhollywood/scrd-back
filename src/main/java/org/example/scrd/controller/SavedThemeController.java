package org.example.scrd.controller;

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
public class SavedThemeController {
    private final SavedThemeService savedThemeService;

    /**
     * 저장한 테마를 볼 수 있는 API
     * */
    @GetMapping
    public ResponseEntity<List<MobileThemeDto>> getSavedThemesWithTimes(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<MobileThemeDto> savedThemes = savedThemeService.getSavedThemesWithAvailableTimes(user.getId(), targetDate);
        return ResponseEntity.ok(savedThemes);
    }

    /**
     * 테마를 저장/취소 (토글) 하는 API
     * */
    @PostMapping("/{themeId}")
    public ResponseEntity<SavedThemeResponse> saveUserTheme(
            @PathVariable Long themeId,
            @AuthenticationPrincipal User user) {
        SavedThemeResponse savedThemeResponse = savedThemeService.savedUserTheme(themeId, user.getId());
        return ResponseEntity.ok(savedThemeResponse);
    }
}

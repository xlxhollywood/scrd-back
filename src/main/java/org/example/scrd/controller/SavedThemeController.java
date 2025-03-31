package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.SavedThemeListResponse;
import org.example.scrd.dto.response.SavedThemeResponse;
import org.example.scrd.service.SavedThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scrd/api")
@RequiredArgsConstructor
public class SavedThemeController {
    private final SavedThemeService savedThemeService;

    /**
     * 테마를 저장/취소 (토글) 하는 API
     * */
    @PostMapping("/save/{themeId}")
    public ResponseEntity<SavedThemeResponse> saveUserTheme(
            @PathVariable Long themeId,
            @AuthenticationPrincipal User user) {
        SavedThemeResponse savedThemeResponse = savedThemeService.savedUserTheme(themeId, user.getId());
        return ResponseEntity.ok(savedThemeResponse);
    }



    /**
     * 저장한 테마를 볼 수 있는 API
     * */
    @GetMapping("/save")
    public ResponseEntity<List<SavedThemeListResponse>> savedUserThemeList(
            @AuthenticationPrincipal User user) {
        List<SavedThemeListResponse> savedThemes = savedThemeService.getSavedThemeList(user.getId());
        return ResponseEntity.ok(savedThemes);
    }

}

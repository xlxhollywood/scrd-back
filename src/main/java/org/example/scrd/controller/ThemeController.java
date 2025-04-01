package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.dto.ThemeDto;
import org.example.scrd.dto.request.ThemeRequest;
import org.example.scrd.dto.response.ThemeAvailableTimeResponse;
import org.example.scrd.service.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scrd/api")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;

    /**
     * 테마 등록(admin 용 API)
     * */
    @PostMapping("/theme")
    public ResponseEntity<Void> addTheme(@RequestBody ThemeRequest request){
        themeService.addTheme(ThemeDto.from(request));
        return ResponseEntity.ok().build();
    }
    /**
     * 테마 수정(admin 용 API)
     * */
    @PutMapping("/theme/{themeId}")
    public ResponseEntity<Void> updateTheme(@PathVariable Long themeId, @RequestBody ThemeRequest request){
        themeService.updateTheme(themeId, ThemeDto.from(request));
        return ResponseEntity.ok().build();
    }

    /**
     * 전체 테마를 불러오는 API
     * */
//    @GetMapping("/theme")
//    public ResponseEntity<List<ThemeDto>> getThemes() {
//        List<ThemeDto> themes = themeService.getAllThemes();
//        return ResponseEntity.ok(themes);
//    }

    /**
     * 특정 테마를 불러오는 API
     * */
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<ThemeDto> getTheme(@PathVariable Long themeId) {
        ThemeDto theme = ThemeDto.toDto(themeService.getThemeById(themeId));
        return ResponseEntity.ok(theme);
    }


    /**
     * 특정 테마의 ID와 날짜로 특정 날짜의 테마 예약 가능 시간대를 가져와 줌.
     * */
    @GetMapping("/theme/{themeId}/available-times")
    public ResponseEntity<ThemeAvailableTimeResponse> getAvailableTimesByDate(
            @PathVariable Long themeId,
            @RequestParam String date // "yyyy-MM-dd" 형식 가정
    ) {
        List<String> availableTime = themeService.getAvailableTimesByDate(themeId, date);
        ThemeAvailableTimeResponse themeAvailableTimeResponse = new ThemeAvailableTimeResponse(date,availableTime);
        return ResponseEntity.ok(themeAvailableTimeResponse);
    }

    /**
     키워드 기반으로 매장이나 테마를 검색해 줌.
     * */
    @GetMapping("/theme/search")
    public ResponseEntity<List<ThemeDto>> searchThemes(@RequestParam String keyword) {
        List<ThemeDto> results = themeService.searchThemes(keyword);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/theme")
    public ResponseEntity<List<ThemeDto>> getThemes(@RequestParam(required = false) String sort) {
        List<ThemeDto> themes;
        if ("rating".equalsIgnoreCase(sort)) {
            themes = themeService.getThemesSortedByRating();
        } else {
            themes = themeService.getAllThemes();
        }

        return ResponseEntity.ok(themes);
    }






}

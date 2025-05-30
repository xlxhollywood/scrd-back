package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.dto.LocationCountDto;
import org.example.scrd.dto.MobileThemeDto;
import org.example.scrd.dto.ThemeDto;
import org.example.scrd.dto.request.ThemeRequest;
import org.example.scrd.dto.response.ThemeAvailableTimeResponse;
import org.example.scrd.service.ThemeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scrd/api")
@RequiredArgsConstructor
@Slf4j
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



    @GetMapping("/theme/{themeId}")
    public ResponseEntity<ThemeDto> getTheme(@PathVariable Long themeId) {
        ThemeDto theme = ThemeDto.toDto(themeService.getThemeById(themeId));
        return ResponseEntity.ok(theme);
    }


    /**
     * 특정 테마를 불러오는 API
     * */
    @GetMapping("web/theme/{themeId}")
    public ResponseEntity<ThemeDto> getThemeDetail (@PathVariable Long themeId) {
        ThemeDto theme = ThemeDto.toWebDto(themeService.getThemeById(themeId));
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
        System.out.println("called");
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

    // Default  .. 추천 + 평점 많은 순으로 테마 불러오기
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


    /**
     * 필터 조건 기반 테마 조회 API
     * ex: /scrd/api/theme/filter?horror=1&activity=1&minLevel=1.0&maxLevel=3.0&minRating=3.5&location=강남
     */

    @GetMapping("/theme/filter")
    public ResponseEntity<List<MobileThemeDto>> getThemesWithFiltersAndSorting(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer horror,
            @RequestParam(required = false) Integer activity,
            @RequestParam(required = false) Float levelMin,
            @RequestParam(required = false) Float levelMax,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "combined") String sort
    ) {
        List<MobileThemeDto> results = themeService.getThemesByFilterCriteria(
                keyword, horror, activity, levelMin, levelMax, location, date, page, size, sort
        );
        return ResponseEntity.ok(results);
    }


    @GetMapping("theme/location-counts")
    public ResponseEntity<Map<String, Object>> getLocationCounts() {
        return ResponseEntity.ok(themeService.getLocationCountsWithTotal());
    }





}

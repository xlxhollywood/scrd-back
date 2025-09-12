package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.scrd.dto.LocationCountDto;
import org.example.scrd.dto.MobileThemeDto;
import org.example.scrd.dto.ThemeDto;
import org.example.scrd.dto.request.ThemeRequest;
import org.example.scrd.dto.response.ThemeAvailableTimeResponse;
import org.example.scrd.service.ThemeService;
import org.springframework.cache.annotation.Cacheable;
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
@Tag(name = "Theme", description = "테마 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class ThemeController {
    private final ThemeService themeService;

    @Operation(summary = "테마 등록 (관리자)", description = "새로운 방탈출 테마를 등록합니다")
    @PostMapping("/theme")
    public ResponseEntity<Void> addTheme(@RequestBody ThemeRequest request){
        themeService.addTheme(ThemeDto.from(request));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "테마 수정 (관리자)", description = "기존 테마 정보를 수정합니다")
    @PutMapping("/theme/{themeId}")
    public ResponseEntity<Void> updateTheme(@PathVariable Long themeId, @RequestBody ThemeRequest request){
        themeService.updateTheme(themeId, ThemeDto.from(request));
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "테마 상세 조회", description = "특정 테마의 상세 정보를 조회합니다")
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<ThemeDto> getTheme(@PathVariable Long themeId) {
        ThemeDto theme = ThemeDto.toDto(themeService.getThemeById(themeId));
        return ResponseEntity.ok(theme);
    }


    @Operation(summary = "웹용 테마 상세 조회", description = "웹 전용 테마 상세 정보를 조회합니다")
    @GetMapping("web/theme/{themeId}")
    public ResponseEntity<ThemeDto> getThemeDetail (@PathVariable Long themeId) {
        ThemeDto theme = ThemeDto.toWebDto(themeService.getThemeById(themeId));
        return ResponseEntity.ok(theme);
    }

    @Operation(summary = "테마 예약 가능 시간 조회", description = "특정 날짜의 테마 예약 가능 시간대를 조회합니다")
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

    @Operation(summary = "테마 목록 조회", description = "모든 테마 목록을 조회합니다. 정렬 옵션을 지정할 수 있습니다")
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



    @Operation(summary = "테마 필터링 검색", description = "다양한 조건으로 테마를 필터링하여 검색합니다")
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

    @Operation(summary = "지역별 테마 개수 조회", description = "각 지역별 테마 개수와 전체 개수를 조회합니다")
    @GetMapping("theme/location-counts")
    public ResponseEntity<Map<String, Object>> getLocationCounts() {
        return ResponseEntity.ok(themeService.getLocationCountsWithTotal());
    }





}

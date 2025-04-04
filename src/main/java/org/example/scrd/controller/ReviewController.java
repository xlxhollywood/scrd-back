package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.ReviewDto;
import org.example.scrd.dto.request.ReviewRequest;
import org.example.scrd.service.ReviewService;
import org.example.scrd.service.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/scrd/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ThemeService themeService;

    /**
     리뷰 등록 API -> 리스폰스 만들기
     * */
    @PostMapping("/review/{themeId}")
    public ResponseEntity<ApiResponse<Object>> addReview(
            @PathVariable Long themeId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user) {

        Theme theme = themeService.getThemeById(themeId);
        reviewService.addReview(ReviewDto.from(request), user.getId(), theme, request.getTagIds());
        return ResponseEntity.ok(ApiResponse.success());
    }



    /**
     * 내가 쓴 리뷰 보기
     * */
    @GetMapping("/review/{userId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewDto> reviews = reviewService.getReviewListByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 테마의  리뷰 보기
     * */
    @GetMapping("/review/theme/{themeId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByTheme(@PathVariable Long themeId){
        List<ReviewDto> reviews = reviewService.getReviewListByTheme(themeId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/review/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id, user);
        return ResponseEntity.ok(ApiResponse.success());
    }



}


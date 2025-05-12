package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.MyReviewResponseDto;
import org.example.scrd.dto.ReviewCreateRequestDto;
import org.example.scrd.dto.ThemeReviewResponseDto;
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
     리뷰 등록 API
     * */
    @PostMapping("/review/{themeId}")
    public ResponseEntity<ApiResponse<Object>> addReview(
            @PathVariable Long themeId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user) {

        Theme theme = themeService.getThemeById(themeId);
        reviewService.addReview(ReviewCreateRequestDto.from(request), user.getId(), theme, request.getTagIds());
        return ResponseEntity.ok(ApiResponse.success());
    }



    /**
     * 내가 쓴 리뷰 보기
     * */
    @GetMapping("/review")
    public ResponseEntity<List<MyReviewResponseDto>> getReviewsByUser(@AuthenticationPrincipal User user) {
        List<MyReviewResponseDto> myReviews = reviewService.getReviewListByUser(user.getId());
        return ResponseEntity.ok(myReviews);
    }

    /**
     * 테마의  리뷰 보기
     * */
    @GetMapping("/review/theme/{themeId}")
    public ResponseEntity<List<ThemeReviewResponseDto>> getReviewsByTheme(@PathVariable Long themeId){
        List<ThemeReviewResponseDto> themeReviews = reviewService.getReviewListByTheme(themeId);
        return ResponseEntity.ok(themeReviews);
    }

    @DeleteMapping("/review/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id, user);
        return ResponseEntity.ok(ApiResponse.success());
    }


    @GetMapping("/review/count")
    public ResponseEntity<ApiResponse<Long>> countReviewsByAuthenticatedUser(
            @AuthenticationPrincipal User user) {
        long reviewCount = reviewService.countReviewsByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success(reviewCount));
    }


}


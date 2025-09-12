package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.MyReviewResponseDto;
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
@Tag(name = "Review", description = "리뷰 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class ReviewController {
    private final ReviewService reviewService;
    private final ThemeService themeService;

    @Operation(summary = "리뷰 등록", description = "특정 테마에 대한 리뷰를 등록합니다")
    @PostMapping("/review/{themeId}")
    public ResponseEntity<ApiResponse<Object>> addReview(
            @PathVariable Long themeId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user) {

        Theme theme = themeService.getThemeById(themeId);
        reviewService.addReview(ReviewCreateRequestDto.from(request), user.getId(), theme, request.getTagIds());
        return ResponseEntity.ok(ApiResponse.success());
    }



    @Operation(summary = "내가 작성한 리뷰 조회", description = "현재 로그인한 사용자가 작성한 모든 리뷰를 조회합니다")
    @GetMapping("/review/my")
    public ResponseEntity<List<MyReviewResponseDto>> getReviewsByUser(@AuthenticationPrincipal User user) {
        List<MyReviewResponseDto> myReviews = reviewService.getReviewListByUser(user.getId());
        return ResponseEntity.ok(myReviews);
    }

    @Operation(summary = "테마별 리뷰 조회", description = "특정 테마에 대한 모든 리뷰를 조회합니다")
    @GetMapping("/review/theme/{themeId}")
    public ResponseEntity<List<ThemeReviewResponseDto>> getReviewsByTheme(@PathVariable Long themeId){
        List<ThemeReviewResponseDto> themeReviews = reviewService.getReviewListByTheme(themeId);
        return ResponseEntity.ok(themeReviews);
    }

    @Operation(summary = "리뷰 삭제", description = "내가 작성한 리뷰를 삭제합니다")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "내 리뷰 작성 개수 조회", description = "현재 로그인한 사용자가 작성한 리뷰 개수를 조회합니다")
    @GetMapping("/review/count")
    public ResponseEntity<ApiResponse<Long>> countReviewsByAuthenticatedUser(
            @AuthenticationPrincipal User user) {
        long reviewCount = reviewService.countReviewsByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success(reviewCount));
    }


}


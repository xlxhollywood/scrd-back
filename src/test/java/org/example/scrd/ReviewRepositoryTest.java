package org.example.scrd;

import org.example.scrd.domain.Review;
import org.example.scrd.repo.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;


    @Test
    void searchReview_withTextKeyword() {
        String keyword = "방탈출"; // 또는 "언어유희", "신기할 정도" 등
        Long userId = null;
        Long themeId = null;

        List<Review> reviews = reviewRepository.searchReviews(keyword, userId, themeId);

        System.out.println("🔍 검색 결과:");
        for (Review review : reviews) {
            System.out.println("📝 " + review.getText());
        }
    }


    @Test
    void getAverageScoreByThemeId_평균조회_성공() {
        Long themeId = 1L; // 👉 실제 DB에 존재하는 테마 ID로 바꿔줘야 함

        double average = reviewRepository.getAverageScoreByThemeId(themeId);
        System.out.println("📊 평균 평점 for themeId = " + themeId + " : " + average);
    }
}

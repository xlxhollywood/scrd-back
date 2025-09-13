package org.example.scrd;

import org.example.scrd.domain.RefreshToken;
import org.example.scrd.domain.ThemeDocument;
import org.example.scrd.repo.RefreshTokenRepository;
import org.example.scrd.repo.ThemeMongoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Tag("integration")
public class ScrdApplicationTests {

    @Autowired
    private ThemeMongoRepository themeMongoRepository;


    @Test
    @DisplayName("id=289 도큐먼트의 예약 가능 시간대 출력")
    void testPrintAvailableTimes() {

        List<ThemeDocument> foundList = themeMongoRepository.findByThemeId(290);
        if (foundList.isEmpty()) {
            System.out.println("id=290인 도큐먼트가 존재하지 않습니다.");
        } else {
            // 여러 문서를 전부 순회하며 출력
            System.out.println("==== themeId=290 문서 총 " + foundList.size() + "개 발견 ====");
            for (ThemeDocument doc : foundList) {
                System.out.println("---- _id: " + doc.get_id()
                        + ", date=" + doc.getDate()
                        + ", title=" + doc.getTitle()
                        + " ----");
                doc.getAvailableTimes().forEach(System.out::println);
            }
        }

    }

    @Test
    @DisplayName("reservation 컬렉션 전체 문서 디버깅")
    void debugAllDocuments() {
        List<ThemeDocument> allDocs = themeMongoRepository.findAll();
        System.out.println("=== debugAllDocuments: Found " + allDocs.size() + " docs ===");

        for (ThemeDocument doc : allDocs) {
            System.out.println("- _id=" + doc.get_id()
                    + ", themeId=" + doc.getThemeId()
                    + ", title=" + doc.getTitle()
                    + ", date=" + doc.getDate());
        }
    }



}

package org.example.scrd.repo;

import org.bson.types.ObjectId;
import org.example.scrd.domain.ThemeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThemeMongoRepository extends MongoRepository<ThemeDocument, ObjectId> {

    // 자바 필드 이름인 themeId 로 메서드 작성
    List<ThemeDocument> findByThemeId(Integer themeId);
    Optional<ThemeDocument> findByThemeIdAndDate(Integer themeId, String date);




}


package org.example.scrd.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;


@Document(collection = "reservation")  // 꼭 있어야 함!
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemeDocument {
    @Id
    private ObjectId _id;  // MongoDB _id

    @Field("id")        // <-- MongoDB에서 이 필드는 "id"로 저장/조회됨
    private Integer themeId;  // 테마 ID (ex: 289)
    private String title;
    private String date;
    private List<String> availableTimes;
    private String location;
    private String branch;
    private String brand;
    private Date updatedAt;
    private Date expireAt;
}

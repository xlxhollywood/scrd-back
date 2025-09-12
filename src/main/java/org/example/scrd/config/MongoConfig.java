package org.example.scrd.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient(
            @Value("${spring.data.mongodb.uri}") String uri,
            // DB명이 URI에 없다면 이 값이 사용됩니다. 있으면 아래 factory에서 무시 또는 동일 값 사용.
            @Value("${spring.data.mongodb.database:scrd}") String databaseName
    ) {
        ConnectionString cs = new ConnectionString(uri);

        MongoClientSettings settings = MongoClientSettings.builder()
                // URI 우선 적용
                .applyConnectionString(cs)
                // Stable API (Atlas V1)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                // 앱 소켓 타임아웃 (URI에 없어도 여기서 커버)
                .applyToSocketSettings(b -> b
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS))
                // 클러스터 서버 선택 타임아웃
                .applyToClusterSettings(b -> b
                        .serverSelectionTimeout(15, TimeUnit.SECONDS))
                .build();

        return MongoClients.create(settings);
    }


    @Bean
    // MongoDatabaseFactory 연결된 DB 인스턴스 생성 (DB 선택 포함)
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        // 스프링이 제공하는 SimpleMongoClientDatabaseFactory를 사용
        // DB 이름은 "scrd"로 가정
        return new SimpleMongoClientDatabaseFactory(mongoClient, "scrd");
    }

    @Bean
    // MongoTemplate 실제로 DB에 쿼리 보내는 도구 (CRUD 수행)
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        // 여기서 만든 MongoTemplate 빈 이름이 "mongoTemplate"가 되고,
        // 스프링 데이터 Mongo Repository가 사용하게 됩니다.
        return new MongoTemplate(mongoDatabaseFactory);
    }
}

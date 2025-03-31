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

@Configuration
public class MongoConfig {

    @Bean
    // MongoDB 클러스터 연결 설정
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:wntkfkd11!@cluster0.fvzvl.mongodb.net/scrd?retryWrites=true&w=majority&tls=true");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
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

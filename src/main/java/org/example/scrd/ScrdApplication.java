package org.example.scrd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScrdApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrdApplication.class, args);
    }

}

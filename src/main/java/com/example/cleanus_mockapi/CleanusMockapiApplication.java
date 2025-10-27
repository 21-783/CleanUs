package com.example.cleanus_mockapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 이게 있어야 @Scheduled 메서드가 작동
public class CleanusMockapiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CleanusMockapiApplication.class, args);
    }
}

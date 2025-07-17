package com.example.project_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 이게 있어야 @Scheduled 메서드가 작동
public class ProjectSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectSystemApplication.class, args);
	}
}

package com.chouket370.realtimefrauddetectionsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealTimeFraudDetectionSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealTimeFraudDetectionSystemApplication.class, args);
    }
}


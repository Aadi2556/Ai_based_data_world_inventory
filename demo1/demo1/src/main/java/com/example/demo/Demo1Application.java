package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Demo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }

    @Bean
    public CommandLineRunner printStartupLink() {
        return args -> {
            System.out.println("\n==================================================");
            System.out.println("  Application started successfully!");
            System.out.println("  Open in browser: http://localhost:8080/home");
            System.out.println("==================================================\n");
        };
    }
}
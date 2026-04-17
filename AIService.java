package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class AIService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiServiceUrl;

    public AIService(@Value("${ai.service.url:http://127.0.0.1:8002}") String aiServiceUrl) {
        this.aiServiceUrl = aiServiceUrl;
    }

    public String askQuestion(String question, Integer machineId) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "question", question,
                    "machine_id", machineId != null ? machineId : 0
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    aiServiceUrl + "/ask",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map body = response.getBody();
            return body != null ? (String) body.get("answer") : "No answer returned.";

        } catch (Exception e) {
            System.err.println("❌ AI service error: " + e.getMessage());
            e.printStackTrace();
            return "AI service error: " + e.getMessage();
        }
    }
}
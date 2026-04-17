package com.example.demo.controller;

import com.example.demo.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/maintenance-ask")
    public Map<String, String> askMaintenance(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        Integer machineId = request.containsKey("machineId") ? (Integer) request.get("machineId") : null;

        String answer = aiService.askQuestion(question, machineId);
        return Map.of("answer", answer);
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "ai-chat";
    }
}
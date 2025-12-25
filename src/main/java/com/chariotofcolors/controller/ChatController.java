package com.chariotofcolors.controller;

import com.chariotofcolors.model.ChatRequest;
import com.chariotofcolors.model.ChatResponse;
import com.chariotofcolors.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        // System.out.println("ChatController received request: " +
        // request.getMessage());
        // GeminiService geminiService = new GeminiService();
        // geminiService.printAvailableModels();
        return chatService.processChat(request);
    }
}

package com.chariotofcolors.service;

import com.chariotofcolors.model.ChatRequest;
import com.chariotofcolors.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final GeminiService geminiService;

    public ChatService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public ChatResponse processChat(ChatRequest request) {
        String response = geminiService.chat(request.getMessage());
        return new ChatResponse(response);
    }
}

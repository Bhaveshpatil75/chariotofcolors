package com.chariotofcolors.service;

import com.chariotofcolors.model.ChatRequest;
import com.chariotofcolors.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final GeminiService geminiService;
    private final GroqService groqService;

    // A simple in-memory cache for chat history. Key: SessionId (or userId), Value:
    // List of recent message objects
    // In production with multiple instances, use Redis instead.
    private final Map<String, List<Map<String, String>>> chatMemory = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY = 10; // Keep last 10 messages (5 turns)

    public ChatService(GeminiService geminiService, GroqService groqService) {
        this.geminiService = geminiService;
        this.groqService = groqService;
    }

    public ChatResponse processChat(ChatRequest request) {
        String responseText = "";

        // Extract or generate a session ID (if frontend doesn't send one, tie to a
        // single session for now just to demo memory)
        String sessionId = request.getUserId() != null && !request.getUserId().trim().isEmpty() ? request.getUserId()
                : "default_session";

        // Get or create history
        List<Map<String, String>> history = chatMemory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // Add User message
        history.add(Map.of("role", "user", "content", request.getMessage()));

        try {
            // Attempt to get response from Groq (Fastest) passing history
            responseText = groqService.chat(history);

        } catch (Exception e1) {
            System.err.println("GroqService failed: " + e1.getMessage() + ". Falling back to Gemini...");

            try {
                // Fallback to Gemini passing history
                responseText = geminiService.chat(history);
            } catch (Exception e2) {
                System.err.println("GeminiService also failed: " + e2.getMessage());
                responseText = "I am currently experiencing technical difficulties. Please try again later.";
            }
        }

        // Final safety net just in case empty responses bubble up
        if (responseText == null || responseText.trim().isEmpty()) {
            responseText = "I am currently experiencing technical difficulties. Please try again later.";
        }

        // Add AI response to history
        history.add(Map.of("role", "assistant", "content", responseText));

        // Trim history if it gets too large
        if (history.size() > MAX_HISTORY) {
            history = history.subList(history.size() - MAX_HISTORY, history.size());
            chatMemory.put(sessionId, new ArrayList<>(history));
        }

        return new ChatResponse(responseText);
    }
}

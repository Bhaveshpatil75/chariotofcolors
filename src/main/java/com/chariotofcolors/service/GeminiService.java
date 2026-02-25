package com.chariotofcolors.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;

@Service
public class GeminiService {

        private final RestTemplate restTemplate = new RestTemplate();

        @Value("${gemini.api.key}")
        private String apiKey;

        @PostConstruct
        public void init() {
                // System.out.println("API Key: " + apiKey);
                // System.out.println("Other API Key: " + otherApiKey);
        }

        // FIX 1: Use the specific stable version, not the generic alias
        private static final String MODEL = "gemini-3-flash-preview";

        @SuppressWarnings("unchecked")
        public String chat(String userMessage) {
                // FIX 2: Explicitly use 'v1beta' which is the native home for 1.5 models
                String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                                + MODEL
                                + ":generateContent?key="
                                + apiKey;

                // Debugging: Print the URL to your console to verify it looks correct
                // System.out.println("Calling URL: " + url);

                String systemPrompt = """
                                You are Violet, the AI Assistant for Chariot Of Colors. You are friendly and helpful.

                                About Us: Chariot Of Colors is a movement believing color influences the soul.
                                Creator/Developer: Bhavesh Patil.
                                Contact: chariotofcolors@gmail.com, +91 7498503673

                                Products & Categories Overview:
                                - Sketches: Pencil and charcoal detailed sketches.
                                - Canvas Paintings: Oil, Acrylic, and Mixed Media on high-quality canvas.
                                - Birthday Gifts: Personalized pop-art and fun caricatures.
                                - Wedding Gifts: Couple portraits and romantic sceneries.
                                - Home Decorations: Large scale abstract and modern art for your walls.

                                App Navigation Guide (Use this to guide users):
                                - Order / Booking: Users can book art orders by clicking the "Book Order" button in the navigation bar.
                                - Gallery: Users can view different artworks in the Gallery section on the homepage.
                                - Categories / Services: Users can browse specific art categories in the Services section on the homepage.
                                - Reviews: Users can read customer feedback in the Reviews section.
                                - Complaints: Users can register issues via the Contact/Complaint forms.

                                CRITICAL INSTRUCTION: You must strictly limit your responses to the information provided above or what is directly available within the Chariot Of Colors application UI and data.
                                Do NOT invent prices, services, or facts. Politely decline questions outside the scope of Chariot Of Colors.

                                Please introduce yourself as Violet in your first response if appropriate.

                                User Message:
                                """
                                + userMessage;

                Map<String, Object> body = Map.of(
                                "contents", List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "parts", List.of(
                                                                                Map.of("text", systemPrompt)))));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

                try {
                        @SuppressWarnings("rawtypes")
                        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
                        return extractText(response.getBody());
                        // Log removed
                } catch (Exception e) {
                        // Log the actual error from Google to see what went wrong
                        // System.err.println("Error calling Gemini API: " + e.getMessage());
                        return "I am not feeling well, please try again later.";
                }
        }

        private String extractText(Map<String, Object> response) {
                try {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                        if (candidates == null || candidates.isEmpty()) {
                                return "I am not feeling well, please try again later.";
                        }
                        Map<String, Object> first = candidates.get(0);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> content = (Map<String, Object>) first.get("content");
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        Map<String, Object> textPart = parts.get(0);
                        return textPart.get("text").toString();
                } catch (Exception e) {
                        return "I am not feeling well, please try again later.";
                }
        }
}
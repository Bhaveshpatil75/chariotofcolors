package com.chariotofcolors.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${groq.api.key}")
    private String apiKey;

    // Use the latest recommended model
    private static final String MODEL = "openai/gpt-oss-120b";
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    @SuppressWarnings({ "unchecked", "null" })
    public String chat(List<Map<String, String>> history) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Groq API key is not configured.");
        }

        String systemPrompt = """
                You are Violet, the AI Assistant for Chariot Of Colors. You are friendly and helpful.

                About Us: Chariot Of Colors is a movement believing color influences the soul. We sell beautiful sketches, canvas paintings, and personalized gifts.
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

                COMMUNICATION STYLE:
                1. Keep your responses VERY SHORT and concise (typically 1-3 sentences max).
                2. NEVER start your message with a greeting, introduction, or pleasantry (like "Hi there", "I'm Violet", "Hello"). Just answer the question directly.
                3. Be conversational, warm, and human-like, but jump straight into the answer.
                4. Avoid overly verbose explanations or long lists. Speak naturally like a real person chatting.
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<Map<String, Object>> messages = new java.util.ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        // Add chat history
        if (history != null) {
            for (Map<String, String> msg : history) {
                messages.add(new java.util.HashMap<>(msg));
            }
        }

        Map<String, Object> body = Map.of(
                "model", MODEL,
                "messages", messages,
                "temperature", 0.7,
                "stream", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, request, Map.class);

        return extractText(response.getBody());
    }

    private String extractText(Map<String, Object> responseBody) throws Exception {
        if (responseBody == null) {
            throw new Exception("Empty response from Groq API");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new Exception("No choices found in Groq response");
        }

        Map<String, Object> firstChoice = choices.get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        if (message == null || message.get("content") == null) {
            throw new Exception("No message content found in Groq response");
        }

        return message.get("content").toString();
    }
}

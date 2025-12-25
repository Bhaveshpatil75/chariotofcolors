package com.chariotofcolors.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String userId; // Optional, for future use or simple session tracking
}

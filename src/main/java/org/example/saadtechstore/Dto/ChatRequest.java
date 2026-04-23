package org.example.saadtechstore.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<MessageHistory> history;

    @Data
    public static class MessageHistory {
        private String role;
        private String content;
    }
}
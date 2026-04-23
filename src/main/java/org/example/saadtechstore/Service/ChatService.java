package org.example.saadtechstore.Service;

import org.example.saadtechstore.Dto.ChatRequest;
import org.example.saadtechstore.Dto.ChatResponse;

public interface ChatService {
    ChatResponse chat(ChatRequest request);

}
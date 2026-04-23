package org.example.saadtechstore.Controller;

import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Dto.ChatRequest;
import org.example.saadtechstore.Dto.ChatResponse;
import org.example.saadtechstore.Service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}
package com.email.writer.controller;

import com.email.writer.model.EmailRequest;
import com.email.writer.service.EmailGeneratorService;
import org.apache.coyote.Response;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private EmailGeneratorService serivice;

    @PostMapping("/generate")
    public ResponseEntity<String > generateEmail(@RequestBody EmailRequest emailRequest){
        try {
            String response = serivice.generateEmailReply(emailRequest);
            return ResponseEntity.ok(response);

        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Error generating email: " + ex.getMessage());
        }

    }
}
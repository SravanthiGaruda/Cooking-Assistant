package com.example.chatbot_backend;

import com.example.chatbot_backend.dialogflow.DialogflowService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/send-message")
public class MessageController {

    private final DialogflowService dialogflowService;

    public MessageController() throws IOException {
        this.dialogflowService = new DialogflowService();
    }

    @CrossOrigin(origins = "<frontend url>") 
    @PostMapping
    public String handleMessage(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response;
        try {
            response = dialogflowService.detectIntent(message);
        } catch (IOException e) {
            e.printStackTrace();
            response = "Error processing message";
        }
        return response;
    }
}

package com.example.chatbot_backend;

import com.example.chatbot_backend.dialogflow.DialogflowService;
import com.example.chatbot_backend.tts.EspeakTTSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/send-message")
public class MessageController {

    private final DialogflowService dialogflowService;
    private final EspeakTTSService espeakTTSService;

    @Autowired
    public MessageController() throws IOException {
        this.dialogflowService = new DialogflowService();
        this.espeakTTSService = new EspeakTTSService();
    }

    @CrossOrigin(origins = "http://localhost:3000") // Allow requests from React frontend
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

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/tts", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateSpeech(@RequestBody Map<String, Object> payload) {
        String text = (String) payload.get("text");
        String voice = (String) payload.getOrDefault("voice", "mb-us2");
        int speed = (int) payload.getOrDefault("speed", 175);
        int pitch = (int) payload.getOrDefault("pitch", 50);

        try {
            byte[] audioContent = espeakTTSService.generateSpeech(text, voice, speed, pitch);
            return ResponseEntity.ok().body(audioContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

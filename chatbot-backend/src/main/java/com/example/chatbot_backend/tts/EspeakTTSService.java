package com.example.chatbot_backend.tts;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class EspeakTTSService {

    public byte[] generateSpeech(String text, String language, int speed, int pitch) throws IOException {
        String audioFilePath = "output.wav";
        String command = String.format("espeak -v %s -s %d -p %d -w %s \"%s\"", language, speed, pitch, audioFilePath, text);

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        Process process = pb.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IOException("Interrupted while waiting for espeak process to complete", e);
        }

        File audioFile = new File(audioFilePath);
        return java.nio.file.Files.readAllBytes(audioFile.toPath());
    }
}

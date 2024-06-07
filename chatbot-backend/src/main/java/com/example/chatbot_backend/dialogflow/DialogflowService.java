package com.example.chatbot_backend.dialogflow;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class DialogflowService {

    private static final String PROJECT_ID = "<project-id>>";
    private static final String LANGUAGE_CODE = "en";

    private SessionsClient sessionsClient;
    private String sessionId;

    public DialogflowService() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider(() -> credentials).build();
        sessionsClient = SessionsClient.create(sessionsSettings);
        sessionId = UUID.randomUUID().toString();
    }

    public String detectIntent(String queryText) throws IOException {
        SessionName session = SessionName.of(PROJECT_ID, sessionId);

        QueryInput queryInput = QueryInput.newBuilder()
                .setText(TextInput.newBuilder()
                        .setText(queryText)
                        .setLanguageCode(LANGUAGE_CODE))
                .build();

        DetectIntentRequest request = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build();

        DetectIntentResponse response = sessionsClient.detectIntent(request);
        QueryResult queryResult = response.getQueryResult();
        String fulfillmentText = queryResult.getFulfillmentText();

        return fulfillmentText;
    }

    public static void main(String[] args) throws IOException {
        DialogflowService dialogflowService = new DialogflowService();
        String queryText = "Hello"; 

        String response = dialogflowService.detectIntent(queryText);
        System.out.println("Response from Dialogflow: " + response);
    }
}
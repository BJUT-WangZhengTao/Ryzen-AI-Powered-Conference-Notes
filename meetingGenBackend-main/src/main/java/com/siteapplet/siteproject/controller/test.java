package com.siteapplet.siteproject.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class test {
    public static void main(String[] args) throws Exception {
        String[] prompts = {
            "What is the meaning of life?",
            "How can I become a better person?",
            "What is the purpose of art?",
            "Why do we dream?"
        };
        String randomPrompt = prompts[new Random().nextInt(prompts.length)];
        System.out.println("Asking: " + randomPrompt);
        System.out.println(getCompletion(randomPrompt));
    }

    public static String getCompletion(String prompt) throws Exception {
        URL url = new URL("http://localhost:1234/v1/chat/completions");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> message1 = new HashMap<>();
        message1.put("role", "system");
        message1.put("content", "Always answer in rhymes.");
        Map<String, Object> message2 = new HashMap<>();
        message2.put("role", "user");
        message2.put("content", prompt);
        data.put("model", "llama2/llama-2-7b-chat");
        data.put("messages", new Object[]{message1, message2});
        data.put("temperature", 0.7);
        data.put("max_tokens", -1);
        data.put("stream", true);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(new ObjectMapper().writeValueAsString(data));
            wr.flush();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if (inputLine.contains("\"content\"")) {
                    JsonNode jsonNode = new ObjectMapper().readTree(inputLine);
                    String content = jsonNode.get("choices").get(0).get("message").get("content").asText();
                    return content;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
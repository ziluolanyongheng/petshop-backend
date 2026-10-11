package com.petshop.petshop_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class BaiduLlmUtil {

    @Value("${baidu.cloud.api-key}")
    private String apiKey;

    @Value("${baidu.cloud.secret-key}")
    private String secretKey;

    @Value("${baidu.cloud.base-url}")
    private String baseUrl;
    
    @Value("${baidu.cloud.app-id}")
    private String appId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getAccessToken() {
        String url = "https://aip.baidubce.com/oauth/2.0/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", apiKey);
        map.add("client_secret", secretKey);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get access token from Baidu.", e);
        }
    }

    public String ask(String question) {
        String accessToken = getAccessToken();
        String url = baseUrl + "/v2/app/conversation/runs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("query", question);
        // Using a new conversation_id for each request for simplicity.
        // For multi-turn conversations, you should manage and reuse this ID.
        body.put("conversation_id", "conv-" + UUID.randomUUID());
        body.put("stream", false);

        try {
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            // Assuming the answer is in `answer` field of the first event in `events` array
            return root.path("events").get(0).path("answer").asText();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to call Baidu LLM API.", e);
        }
    }
} 
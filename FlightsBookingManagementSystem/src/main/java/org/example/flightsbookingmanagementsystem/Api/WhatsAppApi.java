package org.example.flightsbookingmanagementsystem.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppApi {

    @Value("${app.whatsapp.instance}")
    private String instance;

    @Value("${app.whatsapp.token}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String to, String message) {
        String url = "https://api.ultramsg.com/" + instance + "/messages/chat";

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("to", to);
        body.put("body", message);

        restTemplate.postForObject(url, body, String.class);
    }
}
package org.example.flightsbookingmanagementsystem.Controller;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;

    // Added a RequestParam for language selection
    @GetMapping("/concierge/{userId}/{ticketId}")
    public ResponseEntity<?> getTravelGuide(@PathVariable Integer userId, @PathVariable Integer ticketId, @RequestParam(defaultValue = "en") String lang) {

        JsonNode aiResponse = geminiService.getDestinationGuide(ticketId, userId, lang);
        return ResponseEntity.status(200).body(aiResponse);
    }
}
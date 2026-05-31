package org.example.flightsbookingmanagementsystem.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.Flight;
import org.example.flightsbookingmanagementsystem.Model.Ticket;
import org.example.flightsbookingmanagementsystem.Repository.FlightRepository;
import org.example.flightsbookingmanagementsystem.Repository.TicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public JsonNode getDestinationGuide(Integer ticketId, Integer userId, String languageCode) {
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        if (ticket == null || !ticket.getUserId().equals(userId)) {
            throw new ApiException("Valid ticket not found for this user!");
        }

        Flight flight = flightRepository.findFlightById(ticket.getFlightId());
        String destination = flight.getDestinationCity();

        String prompt = "";

        if (languageCode.equalsIgnoreCase("ar")) {
            prompt = "أنت مرشد سياحي خبير. أنا مسافر إلى " + destination + ". " +
                    "يجب أن ترد فقط بتنسيق JSON صالح. لا تستخدم أي علامات markdown مثل ```json. " +
                    "الرد بالكامل ومفاتيح JSON يجب أن تكون باللغة العربية. " +
                    "استخدم هذا الهيكل بالضبط (مصفوفة من الكائنات): " +
                    "{" +
                    "\"المدينة\": \"اسم المدينة\"," +
                    "\"برنامج_الرحلة\": [{\"اليوم\": 1, \"العنوان\": \"...\", \"الوصف\": \"...\"}]," +
                    "\"قائمة_التجهيزات\": [{\"العنصر\": \"...\", \"السبب\": \"...\"}]," +
                    "\"معلومة_ممتعة\": \"...\"" +
                    "}";
        } else {
            prompt = "Act as an expert travel concierge. I am traveling to " + destination + ". " +
                    "You must respond ONLY in valid JSON format. Do not use markdown formatting like ```json. " +
                    "The entire response MUST be written in English. " +
                    "Use exactly this highly structured JSON format (arrays of objects): " +
                    "{" +
                    "\"city\": \"Name of city\"," +
                    "\"itinerary\": [{\"day\": 1, \"title\": \"...\", \"description\": \"...\"}]," +
                    "\"packingList\": [{\"item\": \"...\", \"reason\": \"...\"}]," +
                    "\"funFact\": \"...\"" +
                    "}";
        }

        return callGeminiApi(prompt);
    }

    private JsonNode callGeminiApi(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String safePrompt = prompt.replace("\"", "\\\"");
        String requestBody = "{\"contents\": [{\"parts\":[{\"text\": \"" + safePrompt + "\"}]}]}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + apiKey, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            String aiText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            aiText = aiText.replace("```json", "").replace("```", "").trim();

            return mapper.readTree(aiText);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("Google API Error: " + e.getResponseBodyAsString());
            throw new ApiException("AI Service is temporarily unavailable.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("Failed to parse the AI travel guide.");
        }
    }
}
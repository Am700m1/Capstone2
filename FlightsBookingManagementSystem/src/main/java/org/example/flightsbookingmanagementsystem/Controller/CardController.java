package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Card;
import org.example.flightsbookingmanagementsystem.Service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/get")
    public ResponseEntity<?> getCard() {
        return ResponseEntity.status(200).body(cardService.getCards());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addCard(@RequestBody @Valid Card card) {

        cardService.addCard(card);
        return ResponseEntity.status(200).body(new ApiResponse("Card was added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCard(@PathVariable Integer id, @RequestBody @Valid Card card) {

        cardService.updateCard(id, card);
        return ResponseEntity.status(200).body(new ApiResponse("Card was updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Integer id) {
        cardService.deleteCard(id);
        return ResponseEntity.status(200).body(new ApiResponse("Card was deleted successfully"));
    }


    @GetMapping("/my-cards/{userId}")
    public ResponseEntity<?> getMyCards(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(cardService.getMyCards(userId));
    }


    @PutMapping("/top-up/{userId}/{cardNumber}/{amount}")
    public ResponseEntity<?> topUpCard(@PathVariable Integer userId, @PathVariable Long cardNumber, @PathVariable Integer amount) {
        cardService.topUpCard(userId, cardNumber, amount);
        return ResponseEntity.status(200).body(new ApiResponse("Card topped up successfully. New balance is available."));
    }
}

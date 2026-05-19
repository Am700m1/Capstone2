package org.example.flightsbookingmanagementsystem.Service;


import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.Card;
import org.example.flightsbookingmanagementsystem.Model.User;
import org.example.flightsbookingmanagementsystem.Repository.CardRepository;
import org.example.flightsbookingmanagementsystem.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;


    public List<Card> getCards(){
        List<Card> cards = cardRepository.findAll();

        if(cards.isEmpty()){
            throw new ApiException("No cards exist yet!");
        }

        return cards;
    }


    public void addCard(Card card){
        User user = userRepository.findUserById(card.getUserId());

        if(user == null){
            throw new ApiException("User was not found!");
        }
        cardRepository.save(card);
    }


    public void updateCard(Integer id, Card card){
        Card oldCard = cardRepository.findCardById(id);

        if(oldCard == null){
            throw new ApiException("Card was not found!");
        }

        oldCard.setCardNumber(card.getCardNumber());
        oldCard.setCardHolderName(card.getCardHolderName());
        oldCard.setDueDate(card.getDueDate());
        oldCard.setSecurityCode(card.getSecurityCode());
        oldCard.setBalance(card.getBalance());
        oldCard.setUserId(card.getUserId());

        cardRepository.save(oldCard);
    }


    public void deleteCard(Integer id){
        Card card = cardRepository.findCardById(id);

        if(card == null){
            throw new ApiException("Card was not found!");
        }

        cardRepository.delete(card);
    }


    public List<Card> getMyCards(Integer userId) {
        List<Card> myCards = cardRepository.findCardsByUserId(userId);
        if (myCards.isEmpty()) {
            throw new ApiException("You have no saved cards.");
        }
        return myCards;
    }


    public void topUpCard(Integer userId, Long cardNumber, Integer amount) {
        Card card = cardRepository.findCardByCardNumber(cardNumber);
        if (card == null || !card.getUserId().equals(userId)) {
            throw new ApiException("Valid card not found for this user!");
        }
        if (amount <= 0) {
            throw new ApiException("Top-up amount must be greater than zero!");
        }

        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
    }
}

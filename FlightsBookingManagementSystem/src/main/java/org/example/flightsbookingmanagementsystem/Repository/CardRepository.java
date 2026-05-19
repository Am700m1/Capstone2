package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    Card findCardByCardNumber(Long cardNumber);

    Card findCardById(Integer id);

    List<Card> findCardsByUserId(Integer userId);
}

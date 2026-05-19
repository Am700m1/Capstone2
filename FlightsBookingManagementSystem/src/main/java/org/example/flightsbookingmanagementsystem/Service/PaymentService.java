package org.example.flightsbookingmanagementsystem.Service;


import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Api.EmailApi;
import org.example.flightsbookingmanagementsystem.Model.*;
import org.example.flightsbookingmanagementsystem.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final CardRepository cardRepository;
    private final TicketRepository ticketRepository;
    private final EmailApi emailApi;



    public List<Payment> getPayments(){
        List<Payment> payments = paymentRepository.findAll();

        if (payments.isEmpty()) {
            throw new ApiException("No payments exist yet!");
        }

        return payments;
    }


    public void addPayment(Payment payment){
        User user = userRepository.findUserById(payment.getUserId());
        Flight flight = flightRepository.findFlightById(payment.getFlightId());
        Card card = cardRepository.findCardByCardNumber(payment.getCardNumber());

        if(user == null){
            throw new ApiException("User was not found!");
        }

        if(flight == null){
            throw new ApiException("Flight was not found!");
        }

        if(card == null || !card.getUserId().equals(user.getId())){
            throw new ApiException("Card was not found!");
        }

        if(flight.getPrice() > card.getBalance()){
            throw new ApiException("The balance of the card is not enough to complete this payment!");
        }
        paymentRepository.save(payment);
    }


    public void updatePayment(Integer id, Payment payment){
        Payment oldPayment = paymentRepository.findPaymentById(id);

        if(oldPayment == null){
            throw new ApiException("Payment was not found!");
        }

        oldPayment.setUserId(payment.getUserId());
        oldPayment.setFlightId(payment.getFlightId());
        oldPayment.setCardNumber(payment.getCardNumber());
        oldPayment.setAmount(payment.getAmount());
        oldPayment.setType(payment.getType());

        paymentRepository.save(oldPayment);
    }


    public void deletePayment(Integer id){
        Payment payment = paymentRepository.findPaymentById(id);

        if(payment == null){
            throw new ApiException("Payment was not found!");
        }


        paymentRepository.delete(payment);
    }


    public void paymentProcessing(Integer ticketId, Long cardNumber){
        Card card = cardRepository.findCardByCardNumber(cardNumber);
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        User user = userRepository.findUserById(ticket.getUserId());
        Flight flight = flightRepository.findFlightById(ticket.getFlightId());

        if(ticket == null){
            throw new ApiException("Ticket was not found!");
        }

        if(ticket.getCost()> card.getBalance()){
            throw new ApiException("Card balance is not enough to complete this transaction!");
        }

        if(flight.getSeats() == 0){
            throw new ApiException("There is no available seats!");
        }


        card.setBalance(card.getBalance() - ticket.getCost());
        cardRepository.save(card);

        Payment payment = new Payment();
        payment.setUserId(ticket.getUserId());
        payment.setFlightId(ticket.getFlightId());
        payment.setCardNumber(cardNumber);
        payment.setAmount(ticket.getCost());
        payment.setType("PURCHASE");
        paymentRepository.save(payment);


        flight.setSeats(flight.getSeats()-1);
        flightRepository.save(flight);

        String paymentMessage = String.format(
                "Thank you for booking through our website!\n\n" +
                        "Payment Summary:\n" +
                        "- Flight ID: %d\n" +
                        "- Amount Paid: $%d\n" +
                        "- Card Used: **** **** **** %s\n" +
                        "- Payment Type: %s",
                payment.getFlightId(),
                payment.getAmount(),
                String.valueOf(payment.getCardNumber()).substring(12),
                payment.getType()
        );
        emailApi.sendEmail(user.getEmail(), "Thanks for booking: " + user.getUsername(), paymentMessage);

    }


    public void refundProcessing(Integer ticketId, Long cardNumber){
        Card card = cardRepository.findCardByCardNumber(cardNumber);
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        User user = userRepository.findUserById(ticket.getUserId());
        Flight flight = flightRepository.findFlightById(ticket.getFlightId());
        Payment payment = paymentRepository.findPaymentByUserIdAndFlightId(ticket.getUserId(), ticket.getFlightId());

        if(payment == null){
            throw new ApiException("Payment was not found!");
        }

        if(payment.getType().equalsIgnoreCase("CANCELLED")){
            throw new ApiException("Payment is already cancelled and refunded!");
        }


        card.setBalance(card.getBalance() + ticket.getCost());
        cardRepository.save(card);


        payment.setType("CANCELLED");
        paymentRepository.save(payment);


        flight.setSeats(flight.getSeats()+1);
        flightRepository.save(flight);

        String paymentMessage = String.format(
                "Thank you for using our Website, your payment was refunded successfully!\n\n" +
                        "Refund Summary:\n" +
                        "- Flight ID: %d\n" +
                        "- Amount returned: $%d\n" +
                        "- Card Used: **** **** **** %s\n" +
                        "- Payment Type: %s",
                payment.getFlightId(),
                payment.getAmount(),
                String.valueOf(payment.getCardNumber()).substring(12),
                payment.getType()
        );
        emailApi.sendEmail(user.getEmail(), "Refund message " + user.getUsername(), paymentMessage);

    }


    @Transactional
    public void processAdminMassRefund(Payment payment) {
        Card card = cardRepository.findCardByCardNumber(payment.getCardNumber());
        User user = userRepository.findUserById(payment.getUserId());

        if(card == null) {
            throw new ApiException("No card was found!");
        }

        if(payment.getType().equalsIgnoreCase("CANCELLED")){
            return;
        }

        card.setBalance(card.getBalance() + payment.getAmount());
        cardRepository.save(card);


        payment.setType("CANCELLED");
        paymentRepository.save(payment);

        String message = "We are sorry to inform you that Flight " + payment.getFlightId() +
                " has been cancelled. A refund of $" + payment.getAmount() +
                " has been issued to your card ending in " +
                String.valueOf(payment.getCardNumber()).substring(12);
        emailApi.sendEmail(user.getEmail(), "Flight Cancellation & Refund", message);
    }


    public List<Payment> getMyPayments(Integer userId) {
        List<Payment> payments = paymentRepository.findPaymentsByUserId(userId);
        if (payments.isEmpty()) {
            throw new ApiException("No transaction history found.");
        }
        return payments;
    }
}

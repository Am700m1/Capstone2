package org.example.flightsbookingmanagementsystem.Service;

import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Api.EmailApi;
import org.example.flightsbookingmanagementsystem.Api.WhatsAppApi;
import org.example.flightsbookingmanagementsystem.Model.*;
import org.example.flightsbookingmanagementsystem.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final CardRepository cardRepository;
    private final PaymentService paymentService;
    private final EmailApi emailApi;
    private final WhatsAppApi whatsAppApi;


    public List<Ticket> getTickets(){
        List<Ticket> tickets = ticketRepository.findAll();

        if(tickets.isEmpty()){
            throw new ApiException("No tickets exist now!");
        }

        return tickets;
    }


    public void addTicket(Ticket ticket){
        User user = userRepository.findUserById(ticket.getUserId());
        Flight flight = flightRepository.findFlightById(ticket.getFlightId());
        Passenger passenger = passengerRepository.findPassengerById(ticket.getPassengerId());


        if(user == null){
            throw new ApiException("User was not found!");
        }

        if(flight == null){
            throw new ApiException("Flight was not found!");
        }

        if(passenger == null){
            throw new ApiException("Passenger was not found!");
        }


        ticket.setCost(flight.getPrice() + 50);
        ticketRepository.save(ticket);
    }


    public void updateTicket(Integer id, Ticket ticket){
        Ticket oldTicket = ticketRepository.findTicketById(id);

        if(oldTicket == null){
            throw new ApiException("Ticket was not found!");
        }

        oldTicket.setFlightId(ticket.getFlightId());
        oldTicket.setPassengerId(ticket.getPassengerId());
        oldTicket.setUserId(ticket.getUserId());
        oldTicket.setSeatNumber(ticket.getSeatNumber());
        if(ticket.getCost() == null){
            Flight flight = flightRepository.findFlightById(ticket.getFlightId());
            ticket.setCost(flight.getPrice() + 50);
        }else {
            oldTicket.setCost(ticket.getCost());
        }
        ticketRepository.save(oldTicket);
    }


    public void deleteTicket(Integer id){
        Ticket ticket = ticketRepository.findTicketById(id);

        if(ticket == null){
            throw new ApiException("Ticket was not found!");
        }

        ticketRepository.delete(ticket);
    }

    @Transactional
    public void buyTicket(Integer userId, Integer flightId, Long cardNumber, Passenger passenger){
        User user = userRepository.findUserById(userId);
        Flight flight = flightRepository.findFlightById(flightId);
        Card card = cardRepository.findCardByCardNumber(cardNumber);

        if(user == null){
            throw new ApiException("User was not found!");
        }

        if(!user.getLogin()){
            throw new ApiException("You have to sign in to continue!");
        }

        if(flight == null){
            throw new ApiException("Flight was not found!");
        }

        if(card == null || !card.getUserId().equals(userId)){
            throw new ApiException("Card was not found!");
        }

        if(flight.getSeats() <= 0){
            throw new ApiException("Sorry, this flight is fully booked!");
        }

        Integer ticketCost = flight.getPrice() + 50;


        passenger.setUserId(userId);
        passenger.setFlightId(flightId);
        passengerRepository.save(passenger);


        Ticket newTicket = new Ticket();
        newTicket.setFlightId(flightId);
        newTicket.setPassengerId(passenger.getId());
        newTicket.setUserId(userId);
        newTicket.setCost(ticketCost);
        newTicket.setPurchased(true);
        newTicket.setSeatNumber(flight.getSeats());
        ticketRepository.save(newTicket);

        paymentService.paymentProcessing(newTicket.getId(), cardNumber);

        String ticketMessage = String.format(
                "Hello %s,\n\n" +
                        "Your booking is confirmed! Here are your official ticket details:\n\n" +
                        "🎫 Ticket Summary:\n" +
                        "- Ticket ID: %d\n" +
                        "- Flight ID: %d\n" +
                        "- Passenger ID: %d\n" +
                        "- Total Cost: $%d\n\n" +
                        "We wish you a safe and pleasant journey!",
                user.getUsername(),
                newTicket.getId(),
                newTicket.getFlightId(),
                newTicket.getPassengerId(),
                newTicket.getCost()
        );

        emailApi.sendEmail(user.getEmail(), "Your Ticket: " + user.getUsername(), ticketMessage);
        whatsAppApi.send(passenger.getPhoneNumber(), ticketMessage);

    }

    public void refundTicket(Integer userId, Integer ticketId, Long cardNumber, Integer passengerId){
        User user = userRepository.findUserById(userId);
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        Card card = cardRepository.findCardByCardNumber(cardNumber);
        Passenger passenger = passengerRepository.findPassengerById(passengerId);

        if(user == null){
            throw new ApiException("User was not found!");
        }

        if(!user.getLogin()){
            throw new ApiException("You have to sign in to continue!");
        }

        if(ticket == null || !ticket.getPurchased()){
            throw new ApiException("Ticket was not found!");
        }

        if(card == null || !card.getUserId().equals(userId)){
            throw new ApiException("Card was not found!");
        }

        if(passenger == null){
            throw new ApiException("Passenger was not found!");
        }

        if(!userId.equals(ticket.getUserId())){
            throw new ApiException("Sorry, Ticket was not found!");
        }

        paymentService.refundProcessing(ticketId, cardNumber);
        passengerRepository.delete(passengerRepository.findPassengerById(passengerId));

    }

    public List<Ticket> getMyTickets(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found!");
        }
        List<Ticket> myTickets = ticketRepository.findTicketsByUserId(userId);
        if (myTickets.isEmpty()) {
            throw new ApiException("You have no booked tickets.");
        }
        return myTickets;
    }


    public void checkIn(Integer userId, Integer ticketId){
        User user = userRepository.findUserById(userId);
        Ticket ticket = ticketRepository.findTicketById(ticketId);

        if(user == null){
            throw new ApiException("User was not found!");
        }

        if(ticket == null || !ticket.getUserId().equals(userId)){
            throw new ApiException("Ticket was not found!");
        }

        Flight flight = flightRepository.findFlightById(ticket.getFlightId());
        if(flight == null){
            throw new ApiException("Flight was not found!");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursFromNow = now.plusHours(24);

        if(flight.getDeparture().isBefore(now)){
            throw new ApiException("Check-in is closed. This flight has already departed!");
        }

        if(flight.getDeparture().isAfter(twentyFourHoursFromNow)){
            throw new ApiException("Check-in is not open yet! You can only check in exactly 24 hours before departure.");
        }

        ticket.setIsCheckedIn(true);
        ticketRepository.save(ticket);

    }

}

package org.example.flightsbookingmanagementsystem.Service;


import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Api.EmailApi;
import org.example.flightsbookingmanagementsystem.Api.WhatsAppApi;
import org.example.flightsbookingmanagementsystem.Model.*;
import org.example.flightsbookingmanagementsystem.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void buyTicket(Integer userId, Integer flightId, Long cardNumber, String seatNumber, Passenger passenger){
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

        if (!seatNumber.matches("^[1-9][0-9]?[A-F]$")) {
            throw new ApiException("Invalid seat format! Please choose a valid seat (e.g., 1A, 12C, 24F).");
        }

        Ticket existingTicket = ticketRepository.findTicketByFlightIdAndSeatNumber(flightId, seatNumber);
        if (existingTicket != null) {
            throw new ApiException("Sorry, seat " + seatNumber + " is already reserved! Please choose another seat.");
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
        newTicket.setSeatNumber(seatNumber);
        ticketRepository.save(newTicket);

        paymentService.paymentProcessing(newTicket.getId(), cardNumber);

        String htmlMessage = buildHtmlEmail(user.getUsername(), newTicket, passenger, flight);

        // 2. Generate the PDFs in Memory
        byte[] invoiceBytes = generateInvoicePdf(user, newTicket, card);
        byte[] ticketBytes = generateTicketPdf(passenger, flight, newTicket);

        // 3. Send out the notifications
        emailApi.sendEmailWithAttachments(user.getEmail(), "Your Flight is Confirmed! ✈️", htmlMessage, invoiceBytes, ticketBytes);

        String waMessage = "Hello " + user.getUsername() + ", your flight to " + flight.getDestinationCity() + " is confirmed! Seat: " + seatNumber + ". Check your email for the official ticket and invoice.";
        whatsAppApi.send(passenger.getPhoneNumber(), waMessage);

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

    public List<String> getAvailableSeatsMap(Integer flightId) {
        Flight flight = flightRepository.findFlightById(flightId);

        if (flight == null) {
            throw new ApiException("Flight was not found!");
        }

        List<Ticket> reservedTickets = ticketRepository.findTicketsByFlightId(flightId);

        Set<String> takenSeats = new HashSet<>();
        for (Ticket ticket : reservedTickets) {
            if (ticket.getSeatNumber() != null) {
                takenSeats.add(ticket.getSeatNumber());
            }
        }

        List<String> seatMap = new ArrayList<>();
        String[] columns = {"A", "B", "C", "D", "E", "F"};

        seatMap.add("====== FRONT OF AIRPLANE ======");
        seatMap.add("     [Window]        [Aisle]        [Window]");

        for (int row = 1; row <= 24; row++) {
            StringBuilder rowBuilder = new StringBuilder();

            rowBuilder.append(String.format("Row %02d:  ", row));

            for (int col = 0; col < columns.length; col++) {
                String currentSeat = row + columns[col];

                if (takenSeats.contains(currentSeat)) {
                    rowBuilder.append("[ XX ] ");
                } else {
                    rowBuilder.append(String.format("[ %-3s] ", currentSeat));
                }

                if (col == 2) {
                    rowBuilder.append("   ||   ");
                }
            }
            seatMap.add(rowBuilder.toString());
        }

        seatMap.add("====== BACK OF AIRPLANE ======");

        return seatMap;
    }

    private String buildHtmlEmail(String username, Ticket ticket, Passenger passenger, Flight flight) {
        return "<div style=\"font-family: Arial, sans-serif; background-color: #121212; padding: 40px; color: #ffffff;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #1e1e1e; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.5);\">" +
                "  <div style=\"background-color: #0284c7; padding: 30px; text-align: center;\">" +
                "    <h1 style=\"color: white; margin: 0; font-size: 28px;\">Booking Confirmed! ✈️</h1>" +
                "  </div>" +
                "  <div style=\"padding: 30px;\">" +
                "    <h3 style=\"color: #e0e0e0; font-size: 20px;\">Hello, " + username + "</h3>" +
                "    <p style=\"color: #a0a0a0; line-height: 1.6;\">Your payment was successful and your seat is secured. Please find your official invoice and boarding ticket attached to this email as PDF documents.</p>" +
                "    <div style=\"background-color: #2a2a2a; padding: 20px; border-radius: 8px; margin-top: 25px; border-left: 4px solid #0ea5e9;\">" +
                "      <h4 style=\"margin-top: 0; color: #0ea5e9;\">Quick Overview</h4>" +
                "      <p style=\"margin: 5px 0; color: #cccccc;\"><strong>Passenger:</strong> " + passenger.getName() + "</p>" +
                "      <p style=\"margin: 5px 0; color: #cccccc;\"><strong>Route:</strong> " + flight.getDepartureCity() + " ➔ " + flight.getDestinationCity() + "</p>" +
                "      <p style=\"margin: 5px 0; color: #cccccc;\"><strong>Seat:</strong> <span style=\"background:#0ea5e9; color:white; padding:3px 8px; border-radius:4px;\">" + ticket.getSeatNumber() + "</span></p>" +
                "    </div>" +
                "  </div>" +
                "  <div style=\"background-color: #171717; padding: 20px; text-align: center; color: #777777; font-size: 12px;\">" +
                "    <p>Thank you for choosing our airline. Have a safe flight!</p>" +
                "  </div>" +
                "</div>" +
                "</div>";
    }

    private byte[] generateInvoicePdf(User user, Ticket ticket, Card card) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLUE);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);

            document.add(new Paragraph("OFFICIAL INVOICE", titleFont));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Billed To: " + user.getUsername(), textFont));
            document.add(new Paragraph("Email: " + user.getEmail(), textFont));
            document.add(new Paragraph("Card Used: **** **** **** " + String.valueOf(card.getCardNumber()).substring(12), textFont));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Ticket ID: " + ticket.getId(), textFont));
            document.add(new Paragraph("Total Cost Paid: $" + ticket.getCost(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK)));
            document.add(new Paragraph("Status: PAID", textFont));

            document.close();
        } catch (Exception e) {
            throw new ApiException("Error generating Invoice PDF");
        }
        return out.toByteArray();
    }

    private byte[] generateTicketPdf(Passenger passenger, Flight flight, Ticket ticket) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, Color.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.GRAY);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.BLACK);

            document.add(new Paragraph("BOARDING PASS", titleFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("PASSENGER NAME", labelFont));
            document.add(new Paragraph(passenger.getName().toUpperCase(), dataFont));
            document.add(new Paragraph("PASSPORT NUMBER", labelFont));
            document.add(new Paragraph(passenger.getPassportNumber(), dataFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("FLIGHT ROUTE", labelFont));
            document.add(new Paragraph(flight.getDepartureCity() + " to " + flight.getDestinationCity(), dataFont));

            document.add(new Paragraph("DEPARTURE TIME", labelFont));
            document.add(new Paragraph(flight.getDeparture().toString(), dataFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("SEAT NUMBER", labelFont));
            document.add(new Paragraph(ticket.getSeatNumber(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.RED)));

            document.close();
        } catch (Exception e) {
            throw new ApiException("Error generating Ticket PDF");
        }
        return out.toByteArray();
    }


}

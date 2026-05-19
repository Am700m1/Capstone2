package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Passenger;
import org.example.flightsbookingmanagementsystem.Model.Ticket;
import org.example.flightsbookingmanagementsystem.Service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/get")
    public ResponseEntity<?> getTickets() {
        return ResponseEntity.status(200).body(ticketService.getTickets());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addTicket(@RequestBody @Valid Ticket ticket) {

        ticketService.addTicket(ticket);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket was added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Integer id, @RequestBody @Valid Ticket ticket) {

        ticketService.updateTicket(id, ticket);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket was updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Integer id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket was deleted successfully"));
    }

    @PostMapping("/buy-ticket/{userId}/{flightId}/{cardNumber}")
    public ResponseEntity<?> buyTicket(@PathVariable Integer userId, @PathVariable Integer flightId, @PathVariable Long cardNumber, @RequestBody @Valid Passenger passenger) {

        ticketService.buyTicket(userId, flightId, cardNumber, passenger);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket was purchased successfully!"));
    }


    @PostMapping("/refund/{userId}/{ticketId}/{cardNumber}/{passengerId}")
    public ResponseEntity<?> refundTicket(@PathVariable Integer userId, @PathVariable Integer ticketId, @PathVariable Long cardNumber, @PathVariable Integer passengerId) {
        ticketService.refundTicket(userId, ticketId, cardNumber, passengerId);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket was refunded successfully!"));
    }


    @GetMapping("/my-tickets/{userId}")
    public ResponseEntity<?> getMyTickets(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(ticketService.getMyTickets(userId));
    }


    @PutMapping("/check-in/{userId}/{ticketId}")
    public ResponseEntity<?> checkIn(@PathVariable Integer userId, @PathVariable Integer ticketId) {
        ticketService.checkIn(userId, ticketId);
        return ResponseEntity.status(200).body(new ApiResponse("You were checked in successfully!"));
    }

}

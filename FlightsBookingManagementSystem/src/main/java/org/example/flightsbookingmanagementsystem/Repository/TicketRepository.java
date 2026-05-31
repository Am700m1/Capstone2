package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket findTicketById(Integer id);

    List<Ticket> findTicketsByFlightId(Integer flightId);

    List<Ticket> findTicketsByUserId(Integer userId);


    Ticket findTicketByFlightIdAndSeatNumber(Integer flightId, String seatNumber);
}

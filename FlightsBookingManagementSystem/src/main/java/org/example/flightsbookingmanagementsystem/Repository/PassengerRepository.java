package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    Passenger findPassengerById(Integer id);

    List<Passenger> findPassengersByFlightId(Integer flightId);
}

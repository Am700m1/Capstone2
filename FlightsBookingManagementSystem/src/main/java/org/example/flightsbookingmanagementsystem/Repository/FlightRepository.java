package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    Flight findFlightById(Integer id);

    List<Flight> findFlightsByStatus(String status);

    List<Flight> findFlightsByDepartureCityAndDestinationCityAndDepartureAndStatus(String departureCity, String destinationCity, LocalDateTime departure, String status);

    List<Flight> findFlightsByDepartureCityAndDestinationCityAndDepartureBetweenAndStatus(String departureCity, String destinationCity, LocalDateTime startOfDay, LocalDateTime endOfDay, String status);

    List<Flight> findFlightsByAirlineIdAndStatus(Integer airlineId, String status);
}

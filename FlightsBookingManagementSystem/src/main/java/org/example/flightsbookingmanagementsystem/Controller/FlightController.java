package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Flight;
import org.example.flightsbookingmanagementsystem.Service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/get")
    public ResponseEntity<?> getFlights() {
        return ResponseEntity.status(200).body(flightService.getFlights());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addFlight(@RequestBody @Valid Flight flight) {

        flightService.addFlight(flight);
        return ResponseEntity.status(200).body(new ApiResponse("Flight was added successfully."));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFlight(@PathVariable Integer id, @RequestBody @Valid Flight flight) {

        flightService.updateFlight(id, flight);
        return ResponseEntity.status(200).body(new ApiResponse("Flight was added successfully."));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return ResponseEntity.status(200).body(new ApiResponse("Flight was deleted successfully."));
    }


    @GetMapping("/show-flights")
    public ResponseEntity<?> showScheduledFlights() {
        return ResponseEntity.status(200).body(flightService.showScheduledFlights());
    }


    @GetMapping("/track-flight/{flightId}")
    public ResponseEntity<?> trackFlight(@PathVariable Integer flightId) {
        return ResponseEntity.status(200).body(flightService.trackFlight(flightId));
    }

    @GetMapping("/search/{departureCity}/{destinationCity}/{departureDate}/{filter}")
    public ResponseEntity<?> searchFlights(@PathVariable String departureCity, @PathVariable String destinationCity, @PathVariable LocalDate departureDate, @PathVariable String filter) {
        return ResponseEntity.status(200).body(flightService.searchFlights(departureCity, destinationCity, departureDate, filter));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(flightService.getFlightById(id));
    }


    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<?> getFlightsByAirline(@PathVariable Integer airlineId) {
        return ResponseEntity.status(200).body(flightService.getFlightsByAirline(airlineId));
    }

}

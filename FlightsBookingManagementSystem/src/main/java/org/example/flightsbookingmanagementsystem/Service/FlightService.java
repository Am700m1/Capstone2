package org.example.flightsbookingmanagementsystem.Service;

import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.Admin;
import org.example.flightsbookingmanagementsystem.Model.Airline;
import org.example.flightsbookingmanagementsystem.Model.Airplane;
import org.example.flightsbookingmanagementsystem.Model.Flight;
import org.example.flightsbookingmanagementsystem.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AdminRepository adminRepository;
    private final AirlineRepository airlineRepository;
    private final AirplaneRepository airplaneRepository;

    public List<Flight> getFlights() {
        List<Flight> flights = flightRepository.findAll();

        if (flights.isEmpty()) {
            throw new ApiException("No flights exist yet!");
        }

        return flights;
    }


    public void addFlight(Flight flight) {
        Admin admin = adminRepository.findAdminById(flight.getAdminId());
        Airline airline = airlineRepository.findAirlineById(flight.getAirlineId());
        Airplane  airplane = airplaneRepository.findAirplaneById(flight.getAirplaneId());

        if(admin == null){
            throw new ApiException("Admin was not found!");
        }

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        if(airplane == null){
            throw new ApiException("Airplane was not found!");
        }
        flightRepository.save(flight);
    }


    public void updateFlight(Integer id, Flight flight) {
        Flight oldFlight = flightRepository.findFlightById(id);

        if (oldFlight == null) {
            throw new ApiException("Flight was not found!");
        }

        oldFlight.setAirlineId(flight.getAirlineId());
        oldFlight.setAdminId(flight.getAdminId());
        oldFlight.setDeparture(flight.getDeparture());
        oldFlight.setArrival(flight.getArrival());
        oldFlight.setDepartureCity(flight.getDepartureCity());
        oldFlight.setDestinationCity(flight.getDestinationCity());
        oldFlight.setSeats(flight.getSeats());
        oldFlight.setDuration(flight.getDuration());
        oldFlight.setPrice(flight.getPrice());
        oldFlight.setStatus(flight.getStatus());

        flightRepository.save(oldFlight);
    }


    public void deleteFlight(Integer id) {
        Flight flight = flightRepository.findFlightById(id);

        if (flight == null) {
            throw new ApiException("Flight was not found!");
        }

        flightRepository.delete(flight);
    }


    public List<Flight> showScheduledFlights() {
        List<Flight> flights = flightRepository.findFlightsByStatus("scheduled");

        if (flights.isEmpty()) {
            throw new ApiException("No flights were found!");
        }

        return flights;
    }


    public Flight getFlightById(Integer id) {
        Flight flight = flightRepository.findFlightById(id);
        if (flight == null) {
            throw new ApiException("Flight not found!");
        }
        return flight;
    }


    public Flight trackFlight(Integer flightId) {
        Flight flight = flightRepository.findFlightById(flightId);

        if (flight == null) {
            throw new ApiException("Flight was not found!");
        }

        return flight;
    }


    public List<Flight> searchFlights(String departureCity, String destinationCity, LocalDate departureDate, String filter) {

        LocalDateTime startOfDay = departureDate.atStartOfDay();
        LocalDateTime endOfDay = departureDate.plusDays(1).atStartOfDay();

        List<Flight> flights = flightRepository.findFlightsByDepartureCityAndDestinationCityAndDepartureBetweenAndStatus(departureCity, destinationCity, startOfDay, endOfDay, "scheduled");

        if (flights.isEmpty()) {
            throw new ApiException("No flights were found for this day!");
        }

        if (filter.equalsIgnoreCase("price")) {
            flights.sort(new Comparator<Flight>() {
                @Override
                public int compare(Flight o1, Flight o2) {
                    if (o1.getPrice() == o2.getPrice()) {
                        return 0;
                    }
                    return o1.getPrice() < o2.getPrice() ? -1 : 1;
                }
            });
        }

        if (filter.equalsIgnoreCase("duration")) {
            flights.sort(new Comparator<Flight>() {
                @Override
                public int compare(Flight o1, Flight o2) {
                    if (o1.getDuration() == o2.getDuration()) {
                        return 0;
                    }
                    return o1.getDuration() < o2.getDuration() ? -1 : 1;
                }
            });
        }
        return flights;
    }


    public List<Flight> getFlightsByAirline(Integer airlineId) {
        List<Flight> flights = flightRepository.findFlightsByAirlineIdAndStatus(airlineId, "scheduled");
        if (flights.isEmpty()) {
            throw new ApiException("No scheduled flights found for this airline.");
        }
        return flights;
    }


}

package org.example.flightsbookingmanagementsystem.Service;


import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.Flight;
import org.example.flightsbookingmanagementsystem.Model.Passenger;
import org.example.flightsbookingmanagementsystem.Model.User;
import org.example.flightsbookingmanagementsystem.Repository.FlightRepository;
import org.example.flightsbookingmanagementsystem.Repository.PassengerRepository;
import org.example.flightsbookingmanagementsystem.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;


    public List<Passenger> getPassengers(){
        List<Passenger> passengers = passengerRepository.findAll();

        if(passengers.isEmpty()){
            throw new ApiException("No passengers exist yet!");
        }

        return passengers;
    }



    public void addPassenger(Passenger passenger){
        User user = userRepository.findUserById(passenger.getUserId());
        Flight flight = flightRepository.findFlightById(passenger.getFlightId());

        if(user == null){
            throw new ApiException("User was not found!");
        }

        if(flight == null){
            throw new ApiException("Flight was not found!");
        }
        passengerRepository.save(passenger);
    }



    public void updatePassenger(Integer id, Passenger passenger){
        Passenger oldPassenger = passengerRepository.findPassengerById(id);

        if(oldPassenger == null){
            throw new ApiException("Passenger was not found!");
        }

        oldPassenger.setUserId(passenger.getUserId());
        oldPassenger.setFlightId(passenger.getFlightId());
        oldPassenger.setName(passenger.getName());
        oldPassenger.setAge(passenger.getAge());
        oldPassenger.setPhoneNumber(passenger.getPhoneNumber());
        oldPassenger.setDateOfBirth(passenger.getDateOfBirth());

        passengerRepository.save(oldPassenger);
    }


    public void deletePassenger(Integer id){
        Passenger passenger = passengerRepository.findPassengerById(id);

        if(passenger == null){
            throw new ApiException("Passenger was not found!");
        }

        passengerRepository.delete(passenger);
    }


    public List<Passenger> getPassengersByFlight(Integer flightId){
        List<Passenger> passengers = passengerRepository.findPassengersByFlightId(flightId);

        if(passengers.isEmpty()){
            throw new ApiException("No passengers were found!");
        }

        return passengers;
    }
}

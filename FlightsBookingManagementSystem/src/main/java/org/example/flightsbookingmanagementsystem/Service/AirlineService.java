package org.example.flightsbookingmanagementsystem.Service;


import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.Airline;
import org.example.flightsbookingmanagementsystem.Model.Airplane;
import org.example.flightsbookingmanagementsystem.Repository.AirlineRepository;
import org.example.flightsbookingmanagementsystem.Repository.AirplaneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirplaneRepository airplaneRepository;


    public List<Airline> getAirlines(){
        List<Airline> airlines = airlineRepository.findAll();

        if(airlines.isEmpty()){
            throw new ApiException("No Airlines exist yet!");
        }

        return airlines;
    }


    public void addAirline(Airline airline){
        airlineRepository.save(airline);
    }


    public void updateAirline(Integer id, Airline airline){
        Airline oldAirline = airlineRepository.findAirlineById(id);

        if(oldAirline == null){
            throw new ApiException("Airline was not found!");
        }

        oldAirline.setName(airline.getName());
        airlineRepository.save(oldAirline);
    }



    public void deleteAirline(Integer id){
        Airline airline = airlineRepository.findAirlineById(id);

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        airlineRepository.delete(airline);
    }


    public void addAirplane(Integer airlineId, Airplane airplane){
        Airline airline = airlineRepository.findAirlineById(airlineId);

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        airplaneRepository.save(airplane);
    }


    public void removeAirplane(Integer airlineId, Integer airplaneId){
        Airline airline = airlineRepository.findAirlineById(airlineId);
        Airplane airplane = airplaneRepository.findAirplaneById(airplaneId);

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        if(airplane == null || !airplane.getAirlineId().equals(airlineId)){
            throw new ApiException("Airplane was not found!");
        }

        airplaneRepository.delete(airplane);
    }
}

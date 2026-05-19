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
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;
    private final AirlineRepository airlineRepository;


    public List<Airplane> getAirplanes(){
        List<Airplane> airplanes = airplaneRepository.findAll();

        if(airplanes.isEmpty()){
            throw new ApiException("No airplanes exist yet!");
        }

        return airplanes;
    }


    public void addAirplane(Airplane airplane){
        Airline airline = airlineRepository.findAirlineById(airplane.getAirlineId());

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        airplaneRepository.save(airplane);
    }


    public void updateAirplane(Integer id, Airplane airplane){
        Airplane oldAirplane = airplaneRepository.findAirplaneById(id);
        Airline airline = airlineRepository.findAirlineById(airplane.getAirlineId());

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        if(oldAirplane == null){
            throw new ApiException("Airplane was not found!");
        }

        oldAirplane.setAirlineId(airplane.getAirlineId());
        oldAirplane.setName(airplane.getName());
        oldAirplane.setSeats(airplane.getSeats());
        airplaneRepository.save(oldAirplane);
    }


    public void deleteAirplane(Integer id){
        Airplane airplane = airplaneRepository.findAirplaneById(id);

        if(airplane==null){
            throw new ApiException("Airplane was not found!");
        }

        airplaneRepository.delete(airplane);
    }
}

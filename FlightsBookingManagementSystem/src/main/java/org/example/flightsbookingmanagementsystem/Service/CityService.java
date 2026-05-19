package org.example.flightsbookingmanagementsystem.Service;

import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.City;
import org.example.flightsbookingmanagementsystem.Repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {


    private final CityRepository cityRepository;


    public List<City> getCities(){
        List<City> cities = cityRepository.findAll();

        if(cities.isEmpty()){
            throw new ApiException("No cities exist yet!");
        }

        return cities;
    }


    public void addCity(City city){
        cityRepository.save(city);
    }


    public void updateCity(Integer id, City city){
        City oldCity = cityRepository.findCityById(id);

        if(oldCity == null){
            throw new ApiException("City was not found!");
        }

        oldCity.setCityName(city.getCityName());
        cityRepository.save(oldCity);
    }


    public void deleteCity(Integer id){
        City city = cityRepository.findCityById(id);

        if(city == null){
            throw new ApiException("City was not found");
        }

        cityRepository.delete(city);
    }
}

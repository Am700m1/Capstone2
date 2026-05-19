package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    City findCityById(Integer id);
}

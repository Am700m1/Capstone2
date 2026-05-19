package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.City;
import org.example.flightsbookingmanagementsystem.Service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/city")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;


    @GetMapping("/get")
    public ResponseEntity<?> getCities() {
        return ResponseEntity.status(200).body(cityService.getCities());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addCity(@RequestBody @Valid City city) {

        cityService.addCity(city);
        return ResponseEntity.status(200).body(new ApiResponse("City was added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCity(@PathVariable Integer id, @RequestBody @Valid City city) {

        cityService.updateCity(id, city);
        return ResponseEntity.status(200).body(new ApiResponse("City was updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.status(200).body(new ApiResponse("City was deleted successfully"));
    }
}

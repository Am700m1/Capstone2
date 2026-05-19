package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Airline;
import org.example.flightsbookingmanagementsystem.Model.Airplane;
import org.example.flightsbookingmanagementsystem.Service.AirlineService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/airline")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping("/get")
    public ResponseEntity<?> getAirlines(){
        return ResponseEntity.status(200).body(airlineService.getAirlines());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addAirline(@RequestBody @Valid Airline airline){

        airlineService.addAirline(airline);
        return ResponseEntity.status(200).body(new ApiResponse("Airline was added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAirline(@PathVariable Integer id, @RequestBody @Valid Airline airline){

        airlineService.updateAirline(id, airline);
        return ResponseEntity.status(200).body(new ApiResponse("Airline was updated successfully"));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteAirline(@PathVariable Integer id){
        airlineService.deleteAirline(id);
        return ResponseEntity.status(200).body(new ApiResponse("Airline was deleted successfully"));
    }

    @PostMapping("/add-airplane/{airlineId}")
    public ResponseEntity<?> addAirplane(@PathVariable Integer airlineId, @RequestBody @Valid Airplane airplane){

        airlineService.addAirplane(airlineId, airplane);
        return ResponseEntity.status(200).body(new ApiResponse("Airplane was added successfully"));
    }

    @DeleteMapping("/remove-airplane/{airlineId}/{airplaneId}")
    public ResponseEntity<?> removeAirplane(@PathVariable Integer airlineId,@PathVariable Integer airplaneId){
        airlineService.removeAirplane(airlineId, airplaneId);
        return ResponseEntity.status(200).body(new ApiResponse("Airplane was removed successfully"));
    }
}

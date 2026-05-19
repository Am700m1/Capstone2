package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Passenger;
import org.example.flightsbookingmanagementsystem.Service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passenger")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping("/get")
    public ResponseEntity<?> getPassengers() {
        return ResponseEntity.status(200).body(passengerService.getPassengers());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addPassenger(@RequestBody @Valid Passenger passenger) {

        passengerService.addPassenger(passenger);
        return ResponseEntity.status(200).body(new ApiResponse("Passenger was added successfully."));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePassenger(@PathVariable Integer id, @RequestBody @Valid Passenger passenger) {

        passengerService.updatePassenger(id, passenger);
        return ResponseEntity.status(200).body(new ApiResponse("Passenger was updated successfully."));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable Integer id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.status(200).body(new ApiResponse("Passenger was deleted successfully."));
    }


    @GetMapping("/flight-passengers/{flightId}")
    public ResponseEntity<?> getPassengersByFlight(@PathVariable Integer flightId) {
        return ResponseEntity.status(200).body(passengerService.getPassengersByFlight(flightId));
    }

}

package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Airplane;
import org.example.flightsbookingmanagementsystem.Service.AirplaneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/airplane")
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;


    @GetMapping("/get")
    public ResponseEntity<?> getAirplanes() {
        return ResponseEntity.status(200).body(airplaneService.getAirplanes());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addAirplane(@RequestBody @Valid Airplane airplane) {
        airplaneService.addAirplane(airplane);
        return ResponseEntity.status(200).body(new ApiResponse("Airplane was added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAirplane(@PathVariable Integer id, @RequestBody @Valid Airplane airplane) {
        airplaneService.updateAirplane(id, airplane);
        return ResponseEntity.status(200).body(new ApiResponse("Airplane was updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAirplane(@PathVariable Integer id) {
        airplaneService.deleteAirplane(id);
        return ResponseEntity.status(200).body(new ApiResponse("Airplane was deleted successfully"));
    }
}

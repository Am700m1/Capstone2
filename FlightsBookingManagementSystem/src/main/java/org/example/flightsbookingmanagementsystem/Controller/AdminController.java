package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Admin;
import org.example.flightsbookingmanagementsystem.Model.Airline;
import org.example.flightsbookingmanagementsystem.Model.Flight;
import org.example.flightsbookingmanagementsystem.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/get")
    public ResponseEntity<?> getAdmins() {
        return ResponseEntity.status(200).body(adminService.getAdmins());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestBody @Valid Admin admin) {
        adminService.addAdmin(admin);
        return ResponseEntity.status(200).body(new ApiResponse("Admin was added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody @Valid Admin admin) {
        adminService.updateAdmin(id, admin);
        return ResponseEntity.status(200).body(new ApiResponse("Admin was updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.status(200).body(new ApiResponse("Admin was deleted successfully"));
    }


    @PostMapping("/add-airline/{adminId}")
    public ResponseEntity<?> addAirline(@PathVariable Integer adminId, @RequestBody @Valid Airline airline) {

        adminService.addAirline(adminId, airline);
        return ResponseEntity.status(200).body(new ApiResponse("Airline was added successfully"));

    }


    @PostMapping("/add-flight/{adminId}")
    public ResponseEntity<?> addFlight(@PathVariable Integer adminId, @RequestBody @Valid Flight flight) {
        adminService.addFlight(adminId, flight);
        return ResponseEntity.status(200).body(new ApiResponse("Flight was added successfully"));

    }


    @PutMapping("/change-flight-status/{adminId}/{flightId}/{status}")
    public ResponseEntity<?> changeFlightStatus(@PathVariable Integer adminId, @PathVariable Integer flightId, @PathVariable String status) {
        adminService.changeFlightStatus(adminId, flightId, status);
        return ResponseEntity.status(200).body(new ApiResponse("Flight status was changed successfully"));
    }


    @GetMapping("/get-profit/{adminId}")
    public ResponseEntity<?> profit(@PathVariable Integer adminId) {
        return ResponseEntity.status(200).body(new ApiResponse("The profit from ticket selling is: " + adminService.profit(adminId)));
    }
}

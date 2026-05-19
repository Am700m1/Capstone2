package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.User;
import org.example.flightsbookingmanagementsystem.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.status(200).body(userService.getUsers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody @Valid User user) {

        userService.addUser(user);
        return ResponseEntity.status(200).body(new ApiResponse("User was added successfully."));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody @Valid User user) {

        userService.updateUser(id, user);
        return ResponseEntity.status(200).body(new ApiResponse("User was updated successfully."));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(200).body(new ApiResponse("User was deleted successfully."));
    }


    @PutMapping("/login/{username}/{password}")
    public ResponseEntity<?> login(@PathVariable String username, @PathVariable String password) {
        userService.login(username, password);
        return ResponseEntity.status(200).body(new ApiResponse("You have logged in successfully"));
    }


    @PutMapping("/logout/{username}/{password}")
    public ResponseEntity<?> logout(@PathVariable String username, @PathVariable String password) {
        userService.logout(username, password);
        return ResponseEntity.status(200).body(new ApiResponse("You have logged in successfully"));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {

        userService.register(user);
        return ResponseEntity.status(200).body(new ApiResponse("You have Registered successfully, please login to continue"));
    }

}

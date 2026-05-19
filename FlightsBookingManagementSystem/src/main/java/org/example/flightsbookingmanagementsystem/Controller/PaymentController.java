package org.example.flightsbookingmanagementsystem.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiResponse;
import org.example.flightsbookingmanagementsystem.Model.Payment;
import org.example.flightsbookingmanagementsystem.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/get")
    public ResponseEntity<?> getPayments() {
        return ResponseEntity.status(200).body(paymentService.getPayments());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addPayment(@RequestBody @Valid Payment payment) {

        paymentService.addPayment(payment);
        return ResponseEntity.status(200).body(new ApiResponse("Payment was added successfully."));

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody @Valid Payment payment) {

        paymentService.updatePayment(id, payment);
        return ResponseEntity.status(200).body(new ApiResponse("Payment was updated successfully."));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Payment was deleted successfully."));
    }


    @GetMapping("/my-history/{userId}")
    public ResponseEntity<?> getMyPayments(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(paymentService.getMyPayments(userId));
    }
}

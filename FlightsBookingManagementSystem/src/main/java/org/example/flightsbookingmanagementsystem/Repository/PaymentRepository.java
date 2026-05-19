package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findPaymentById(Integer id);

    List<Payment> findPaymentsByFlightId(Integer flightId);

    Payment findPaymentByUserIdAndFlightId(Integer userId, Integer flightId);

    List<Payment> findPaymentsByFlightIdAndType(Integer flightId, String type);

    List<Payment> findPaymentByType(String type);

    List<Payment> findPaymentsByUserId(Integer userId);
}

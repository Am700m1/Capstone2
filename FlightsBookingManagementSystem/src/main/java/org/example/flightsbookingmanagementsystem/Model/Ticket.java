package org.example.flightsbookingmanagementsystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Flight ID must be filled!")
    @Positive(message = "Flight ID must not be a negative number!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer flightId;


    @NotNull(message = "Passenger ID must be filled!")
    @Positive(message = "Passenger ID must not be a negative number!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer passengerId;


    @NotNull(message = "User ID must be filled!")
    @Positive(message = "User ID must not be a negative number!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer userId;


    @NotNull(message = "Seat number must be included")
    @PositiveOrZero(message = "Seat number cannot be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer seatNumber;

    @Column(columnDefinition = "boolean default false")
    private Boolean purchased;


    @Column(columnDefinition = "boolean default false")
    private Boolean isCheckedIn;


    @Column(columnDefinition = "int default 0", nullable = true)
    private Integer cost;
}

package org.example.flightsbookingmanagementsystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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


    @NotEmpty(message = "Seat number must be included")
    @Column(columnDefinition = "varchar(10)", nullable = false)
    private String seatNumber;


    @Column(columnDefinition = "boolean default false")
    private Boolean purchased;


    @Column(columnDefinition = "boolean default false")
    private Boolean isCheckedIn;


    @Column(columnDefinition = "int default 0", nullable = true)
    private Integer cost;
}

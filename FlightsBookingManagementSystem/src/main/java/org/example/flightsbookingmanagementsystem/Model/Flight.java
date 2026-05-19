package org.example.flightsbookingmanagementsystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Flight {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Airline ID must be included!")
    @Positive(message = "Airline ID must not be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer airlineId;


    @NotNull(message = "Airplane ID must be included!")
    @Positive(message = "Airplane ID must not be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer airplaneId;


    @Positive(message = "Admin ID must not be negative!")
    @Column(columnDefinition = "int")
    private Integer adminId;


    @NotNull(message = "Departure Date and Time must be included!")
    @Column(columnDefinition = "datetime", nullable = false)
    private LocalDateTime departure;


    @NotNull(message = "Arrival Date and Time must be included!")
    @Column(columnDefinition = "datetime", nullable = false)
    private LocalDateTime arrival;


    @NotEmpty(message = "Departure City must be included!")
    @Size(min = 3, max = 20, message = "Destination City name must consist of at least 3 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String departureCity;


    @NotEmpty(message = "Destination City must be included!")
    @Size(min = 3, max = 20, message = "Destination City name must consist of at least 3 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String destinationCity;


    @NotNull(message = "Number of seats must be included!")
    @Positive(message = "Number of seats must not be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer seats;


    @NotNull(message = "Flight Duration must be included!")
    @Positive(message = "Flight Duration must be positive!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer duration;


    @NotNull(message = "Flight price must be included!")
    @Positive(message = "Flight price must not be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer price;


    @NotEmpty(message = "Status must be included!")
    @Size(min = 3, max = 20, message = "Status must consist of at least 3 characters and not more than 20!")
    @Pattern(regexp = "^(scheduled|boarding|departed|diverted|landed|arrived|cancelled|unknown)$")
    @Check(constraints = "status IN ('scheduled', 'boarding', 'departed', 'diverted', 'landed', 'arrived', 'cancelled', 'unknown')")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String status;

}

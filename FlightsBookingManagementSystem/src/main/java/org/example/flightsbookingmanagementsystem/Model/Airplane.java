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
public class Airplane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Airline ID must be included!")
    @Positive(message = "Airline ID must not be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer airlineId;


    @NotEmpty(message = "Airplane name must included!")
    @Size(min = 3, max = 20, message = "Airplane name must consist of at least 3 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String name;


    @NotNull(message = "Number of Seats must be included")
    @PositiveOrZero(message = "Number of seats cannot be negative!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer seats;
}

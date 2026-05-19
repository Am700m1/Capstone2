package org.example.flightsbookingmanagementsystem.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class City {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;


    @NotEmpty(message = "City name must included!")
    @Size(min = 3, max = 20, message = "City name must consist of at least 3 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String cityName;
}

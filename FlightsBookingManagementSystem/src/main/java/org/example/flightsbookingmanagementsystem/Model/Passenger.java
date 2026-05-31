package org.example.flightsbookingmanagementsystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


//    @NotNull(message = "User ID must be filled!")
    @Positive(message = "User ID must not be a negative number!")
    @Column(columnDefinition = "int")
    private Integer userId;


//    @NotNull(message = "Flight ID must be filled!")
    @Positive(message = "Flight ID must not be a negative number!")
    @Column(columnDefinition = "int")
    private Integer flightId;


    @NotEmpty(message = "Name must be included!")
    @Size(min = 3, max = 20, message = "Name must consist of at least 3 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String name;


    @NotNull(message = "Age must be included!")
    @Min(value = 18, message = "Age must be 18 or more!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer age;


    @NotEmpty(message = "Phone Number must be included!")
    @Size(min = 5, max = 20, message = "Phone Number must consist of at least 5 characters and not more than 20!")
    @Pattern(regexp = "^\\+9665\\d{8}$", message = "Phone Number must start with +9665 and consist of 13 digits only!")    @Size(min = 13, max = 13, message = "Phone Number must consist of 13 digits only!")
    @Column(columnDefinition = "varchar(13)", unique = true, nullable = false)
    private String phoneNumber;



    @NotNull(message = "Date of Birth must be included!")
    @Past(message = "Date of Birth must be in the Past!")
    @Column(columnDefinition = "date", nullable = false)
    private LocalDate dateOfBirth;


    @NotEmpty(message = "Gender must be included!")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Gender must be either MALE or FEMALE")
    @Check(constraints = "gender IN ('MALE','FEMALE')")
    @Column(columnDefinition = "varchar(10)", nullable = false)
    private String gender;

    @NotEmpty(message = "Passport number must be included!")
    @Size(min = 8, max = 9, message = "Passport number must consist of 8 or 9 characters")
    @Column(columnDefinition = "varchar(10)", nullable = false, unique = true)
    private String passportNumber;
}

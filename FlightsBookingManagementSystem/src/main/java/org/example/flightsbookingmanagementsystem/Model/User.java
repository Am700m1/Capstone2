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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotEmpty(message = "Username must be included!")
    @Size(min = 5, max = 20, message = "Username must consist of at least 5 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", unique = true, nullable = false)
    private String username;


    @NotEmpty(message = "Email must included!")
    @Size(min = 15, max = 50, message = "Email must consist of at least 15 characters!")
    @Email(message = "Email must be in the right structure!")
    @Column(columnDefinition = "varchar(50)", unique = true, nullable = false)
    private String email;


    @NotEmpty(message = "Password must included!")
    @Size(min = 8, max = 20, message = "Password must consist of at least 8 characters!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!#$%^&+=]).{8,}$", message = "Password must have capital and small characters, digits, and special character!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String password;


    @Column(columnDefinition = "boolean default false")
    private Boolean login;

}

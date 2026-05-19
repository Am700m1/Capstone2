package org.example.flightsbookingmanagementsystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Security Code must be included!")
    @Column(columnDefinition = "bigint", unique = true, nullable = false)
    private Long cardNumber;


    @NotEmpty(message = "Name must be included!")
    @Size(min = 3, max = 20, message = "Name must consist of at least 3 characters and not more than 20!")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String cardHolderName;


    @NotNull(message = "Due Date must be included!")
    @Future(message = "Due Date must be in the future!")
    @Column(columnDefinition = "date", nullable = false)
    private LocalDate dueDate;


    @NotNull(message = "Security Code must be included!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer securityCode;


    @NotNull(message = "Balance must be included!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer balance;


    @NotNull(message = "User ID must be included!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer userId;
}

package org.example.flightsbookingmanagementsystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "User ID must be included!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer userId;


    @NotNull(message = "Flight ID must be included!")
    @Positive(message = "Flight ID must not be negative a negative number!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer flightId;


    @NotNull(message = "Card Number must be included!")
    @Positive(message = "Card Number must not be negative a negative number!")
    @Column(columnDefinition = "bigint", nullable = false)
    private Long cardNumber;


    @NotNull(message = "Amount must be included!")
    @Positive(message = "Amount must not be negative a negative number!")
    @Column(columnDefinition = "int", nullable = false)
    private Integer amount;


    @NotEmpty(message = "Type must be included!")
    @Size(min = 5, max = 10, message = "Type must consist of at least 5 characters and not more than 10!")
    @Pattern(regexp = "^(PURCHASE|CANCELLED)$")
    @Check(constraints = "type IN ('PURCHASE','CANCELLED')")
    @Column(columnDefinition = "varchar(10)", nullable = false)
    private String type;

}

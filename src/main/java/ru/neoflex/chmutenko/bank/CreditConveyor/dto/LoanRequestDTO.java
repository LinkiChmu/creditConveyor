package ru.neoflex.chmutenko.bank.CreditConveyor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.neoflex.chmutenko.bank.CreditConveyor.constraints.BirthDate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
    @NotNull(message="Amount should not be empty")
    @DecimalMin(value="10000.00", message="Amount should be greater than or equal to 10 000")
    private BigDecimal amount;

    @NotNull(message="Term should not be empty")
    @Digits(fraction=0, integer=3, message="Term must consist only of digits and be an integer")
    @Min(value=6L, message="Term should be greater than or equal to 6")
    private int term;

    @Size(min=2, max=30, message="Name should be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message="Name must consist only of Latin letters")
    private String firstName;

    @Size(min=2, max=30, message="Last name should be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message="Last name must consist only of Latin letters")
    private String lastName;

    @Nullable
    @Pattern(regexp="^[a-zA-Z]{2,30}$", message="Middle name must consist only of 2 to 30 Latin letters")
    private String middleName;

    @Pattern(regexp="^[\\w.]{2,50}@[\\w.]{2,20}$", message="Email must match email format")
    private String email;

    @NotNull
    @JsonFormat(pattern="yyyy.MM.dd")
    @DateTimeFormat(pattern="yyyy.MM.dd")
    @BirthDate(message = "Birth date should be no late than 18 years ago")
    private LocalDate birthdate;

    @Pattern(regexp="^[\\d]{4}$", message="Passport series must consist only of 4 digits")
    private String passportSeries;

    @Pattern(regexp="^[\\d]{6}$", message="Passport number must consist only of 6 digits")
    private String passportNumber;
}

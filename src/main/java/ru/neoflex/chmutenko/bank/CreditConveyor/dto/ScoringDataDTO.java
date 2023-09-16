package ru.neoflex.chmutenko.bank.CreditConveyor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.neoflex.chmutenko.bank.CreditConveyor.constraints.BooleanSubset;
import ru.neoflex.chmutenko.bank.CreditConveyor.constraints.GenderSubset;
import ru.neoflex.chmutenko.bank.CreditConveyor.constraints.MaritalStatusSubset;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.Gender;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ScoringDataDTO {

    @NotNull(message = "Amount should not be empty")
    @DecimalMin(value = "10000.00", message = "Amount should be greater than or equal to 10 000")
    private BigDecimal amount;

    @NotNull(message = "Term should not be empty")
    @Digits(fraction = 0, integer = 3, message = "Term must consist only of digits and be an integer")
    @Min(value = 6, message = "Term should be greater than or equal to 6")
    private int term;

    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must consist only of Latin letters")
    private String firstName;

    @Size(min = 2, max = 30, message = "Last name should be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must consist only of Latin letters")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Middle name must consist only of 2 to 30 Latin letters")
    private String middleName;

    @NotNull
    @GenderSubset(anyOf = {Gender.FEMALE, Gender.MALE, Gender.NON_BINARY})
    private Gender gender;

    @NotNull
    @JsonFormat(pattern = "yyyy.MM.dd")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    @Past(message = "Birthdate should be in past")
    private LocalDate birthdate;

    @Pattern(regexp = "^[\\d]{4}$", message = "Passport series must consist only of 4 digits")
    private String passportSeries;

    @Pattern(regexp = "^[\\d]{6}$", message = "Passport number must consist only of 6 digits")
    private String passportNumber;

    @NotNull
    @JsonFormat(pattern = "yyyy.MM.dd")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDate passportIssueDate;

    @Pattern(regexp = "^[\\d]{3}-[\\d]{3}$", message = "Passport issue branch must be like '123-456'")
    private String passportIssueBranch;

    @NotNull
    @MaritalStatusSubset(anyOf = {MaritalStatus.SINGLE, MaritalStatus.MARRIED})
    private MaritalStatus maritalStatus;

    @NotNull(message = "Number of dependents should not be empty")
    @Digits(fraction = 0, integer = 2, message = "Number of dependents must consist only of digits and be an integer")
    @Min(value = 0, message = "Number of dependents should be greater than or equal to 0")
    private int dependentAmount;

    @Valid
    @NotNull
    @JsonProperty("employment")
    private EmploymentDTO employmentDTO;

    @Pattern(regexp = "^[\\d]{20}$")
    private String account;

    @NotNull
    @BooleanSubset(anyOf = {true, false})
    private boolean isInsuranceEnabled;

    @NotNull
    @BooleanSubset(anyOf = {true, false})
    private boolean isSalaryClient;
}

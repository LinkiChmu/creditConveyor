package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.PaymentScheduleElement;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentPosition;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.Gender;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.MaritalStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.util.CalculationUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationService {

    private final CalculationUtil calculator;

    @Value("${loanOffer.baseRate}")
    private BigDecimal baseRate;

    @Value("${loanOffer.insurance}")
    private BigDecimal insuranceAmount;


    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO, EmploymentDTO employmentDTO) {

        BigDecimal totalRate = calculateTotalRate(baseRate, scoringDataDTO, employmentDTO);
        log.info("calculateTotalRate() returned totalRate: %s".formatted(totalRate.toString()));

        BigDecimal requestedAmount = scoringDataDTO.getAmount();
        int term = scoringDataDTO.getTerm();
        boolean insuranceEnabled = scoringDataDTO.isInsuranceEnabled();
        boolean salaryClient = scoringDataDTO.isSalaryClient();

        BigDecimal totalAmount = calculateTotalAmount(
                scoringDataDTO.getAmount(), insuranceEnabled, insuranceAmount);

        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(
                totalAmount, totalRate, scoringDataDTO.getTerm());

        BigDecimal psk = calculatePSK(totalRate, scoringDataDTO.getTerm());

        log.info(("Starting to creat CreditDTO() with params amount %s, term %d, monthlyPayment %s, " +
                "rate %s, psk %s, isInsuranceEnabled %s, isSalaryClient %s")
                .formatted(requestedAmount.toString(),
                        term,
                        monthlyPayment.toString(),
                        totalRate.toString(),
                        psk.toString(),
                        insuranceEnabled,
                        salaryClient));
        CreditDTO dto = CreditDTO.builder()
                .amount(requestedAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(totalRate)
                .psk(psk)
                .isInsuranceEnabled(insuranceEnabled)
                .isSalaryClient(salaryClient)
                .build();

        dto.setPaymentSchedule(makeSchedule(requestedAmount, term, monthlyPayment, totalRate));
        return dto;
    }

    private BigDecimal calculateTotalRate(BigDecimal baseRate,
                                          ScoringDataDTO scoringDataDTO,
                                          EmploymentDTO employmentDTO) {
        log.info("Starting calculateTotalRate() with params baseRate %s, scoringDataDTO %s,  employmentDTO %s"
                .formatted(baseRate.toString(), scoringDataDTO, employmentDTO));

        BigDecimal rate = setByEmploymentStatus(employmentDTO.getEmploymentStatus(), baseRate);
        rate = setByPosition(employmentDTO.getPosition(), rate);
        rate = setByMaritalStatus(scoringDataDTO.getMaritalStatus(), rate);
        rate = setByDependentsAmount(scoringDataDTO.getDependentAmount(), rate);
        rate = setByGenderAndAge(scoringDataDTO.getGender(), calculator.countAge(scoringDataDTO.getBirthdate()), rate);
        rate = setByInsuranceAndSalaryClient(scoringDataDTO.isInsuranceEnabled(), scoringDataDTO.isSalaryClient(), rate);

        log.info("Method calculateTotalRate returned totalRate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal calculateTotalAmount(BigDecimal amount,
                                            boolean isInsuranceEnabled,
                                            BigDecimal insuranceAmount) {
        BigDecimal totalAmount = calculator.calculateTotalAmount(amount, isInsuranceEnabled, insuranceAmount);
        log.info("method calculateTotalAmount() calculated totalAmount: %s".formatted(totalAmount.toString()));
        return totalAmount;
    }

    private BigDecimal calculatePSK(BigDecimal rate, int term) {
        log.info("Starting calculatePSK() with params rate %s,term %d"
                .formatted(rate.toString(), term));

        BigDecimal monthlyRate = calculator.monthlyRate(rate);
        BigDecimal psk = monthlyRate.multiply(BigDecimal.valueOf(12));

        log.info("method calculatePSK() calculated PSK: %s".formatted(psk.toString()));
        return psk;
    }

    private BigDecimal setByEmploymentStatus(EmploymentStatus status, BigDecimal rate) {
        log.info("Starting setByEmploymentStatus() with params EmploymentStatus %s, rate %s"
                .formatted(status, rate.toString()));
        if (status == EmploymentStatus.SELF_EMPLOYED) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        if (status == EmploymentStatus.BUSINESS_OWNER) {
            rate = rate.add(BigDecimal.valueOf(3L));
        }
        log.info("setByEmploymentStatus returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByPosition(EmploymentPosition position, BigDecimal rate) {
        log.info("Starting setByPosition() with params EmploymentPosition %s, rate %s"
                .formatted(position, rate.toString()));
        if (position == EmploymentPosition.TOP_MANAGER) {
            rate = rate.subtract(BigDecimal.valueOf(3L));
        }
        if (position == EmploymentPosition.MIDDLE_MANAGER) {
            rate = rate.subtract(BigDecimal.valueOf(2L));
        }
        log.info("setByPosition() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByMaritalStatus(MaritalStatus status, BigDecimal rate) {
        log.info("Starting setByMaritalStatus() with params status %s, rate %s"
                .formatted(status, rate.toString()));
        if (status == MaritalStatus.MARRIED) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        if (status == MaritalStatus.DIVORCED) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        log.info("setByMaritalStatus() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByDependentsAmount(int dependentsAmount, BigDecimal rate) {
        log.info("Starting setByDependentsAmount() with params dependentsAmount %d, rate %s"
                .formatted(dependentsAmount, rate.toString()));
        if (dependentsAmount > 1) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        log.info("setByDependentsAmount() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByGenderAndAge(Gender gender, int age, BigDecimal rate) {
        log.info("Starting setByGenderAndAge() with params gender %s, age %d, rate %s"
                .formatted(gender, age, rate.toString()));
        if (gender == Gender.FEMALE && (age >= 35 && age <= 60)) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        if (gender == Gender.MALE && (age >= 30 && age <= 55)) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        if (gender == Gender.NON_BINARY) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        log.info("setByGenderAndAge() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByInsuranceAndSalaryClient(boolean isInsuranceEnabled,
                                                     boolean isSalaryClient,
                                                     BigDecimal rate) {
        log.info(("Starting setByInsuranceAndSalaryClient() with params " +
                "isInsuranceEnabled %s, isSalaryClient %s, rate %s")
                .formatted(isInsuranceEnabled, isSalaryClient, rate.toString()));
        if (isInsuranceEnabled) {
            rate = rate.subtract(BigDecimal.valueOf(3L));
        }
        if (isSalaryClient) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        log.info("setByInsuranceAndSalaryClient() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private List<PaymentScheduleElement> makeSchedule(BigDecimal amount,
                                                    int term,
                                                    BigDecimal monthlyPayment,
                                                    BigDecimal rate) {

        //List<PaymentScheduleElement> schedule = new ArrayList<>();
        LocalDate startDate = LocalDate.now();

        List<PaymentScheduleElement> schedule = Stream.iterate(1, i -> ++i).limit(term)
                .map(i -> {
                    var current = startDate.plusMonths(i);

                    PaymentScheduleElement el = PaymentScheduleElement.builder()
                            .number(i)
                            .date(current)
                            .totalPayment(monthlyPayment)
                            .build();
                return el;
                })
                .map(el -> el.getRemainingDebt())
                .reduce(amount, )
                .map(el -> {

                    el.setInterestPayment(calculator.calculateInterestPayment(el.getDate(), el.getRemainingDebt(), rate));
                    el.setDebtPayment(monthlyPayment.subtract(el.getInterestPayment()));

                    return el;
                })


//
//                    el.setRemainingDebt(calculator.calculateRemainingDebt(balanceDebt, el.getDebtPayment()));
//                    el.setBalanceDebt(el.getRemainingDebt());
                .toList();
                //.collect(Collectors.toList());
                            //.interestPayment(CalculationUtil::calculateInterestPayment(current, currentBalance, rate))


//        for (int i = 1; i < term + 1; i++) {
//            PaymentScheduleElement element = new PaymentScheduleElement();
//            element.setNumber(i);
//
//            LocalDate currentDate = startDate.plusMonths(i);
//            element.setDate(currentDate);
//
//            element.setTotalPayment(monthlyPayment);
//
//
//            BigDecimal interestPayment = calculator.calculateInterestPayment(currentDate, balanceDebt, rate);
//            element.setInterestPayment(interestPayment);
//
//            BigDecimal debtPayment = calculator.calculateDebtPayment(monthlyPayment, interestPayment);
//            element.setDebtPayment(debtPayment);
//
//            BigDecimal remainingDebt = calculator.calculateRemainingDebt(balanceDebt, debtPayment);
//            element.setRemainingDebt(remainingDebt);
//
//            balanceDebt = remainingDebt;
//            log.info("balanceDebt updated: %s".formatted(balanceDebt.toString()));
//
//            schedule.add(element);
//            log.info("added %s".formatted(element));
  //      }
        return schedule;
    }
}
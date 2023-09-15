package ru.neoflex.chmutenko.bank.CreditConveyor.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.LoanDeniedException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler()
    public ResponseEntity<String> loanDeniedExceptionHandler(LoanDeniedException e) {
        return new ResponseEntity<>("Sorry, loan denied", HttpStatus.UNPROCESSABLE_ENTITY);
    }

}

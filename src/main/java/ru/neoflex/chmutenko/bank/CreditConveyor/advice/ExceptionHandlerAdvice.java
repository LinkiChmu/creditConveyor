package ru.neoflex.chmutenko.bank.CreditConveyor.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.DataNotValidException;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.LoanDeniedException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler()
    public ResponseEntity<String> loanDeniedExceptionHandler(LoanDeniedException e) {
        return new ResponseEntity<>("Sorry, loan denied", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler()
    public ResponseEntity<String> dataNotValidExceptionHandler(DataNotValidException e) {
        return new ResponseEntity<>("Data not valid: %s".formatted(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler()
    public ResponseEntity<String> enumNotValidExceptionHandler(HttpMessageNotReadableException e) {
        return new ResponseEntity<>("Data not valid: value not from the list", HttpStatus.BAD_REQUEST);
    }

}

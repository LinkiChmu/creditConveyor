package ru.neoflex.chmutenko.bank.CreditConveyor.util;

public class RequestNotValidException extends RuntimeException{
    public RequestNotValidException(String message) {
        super(message);
    }
}

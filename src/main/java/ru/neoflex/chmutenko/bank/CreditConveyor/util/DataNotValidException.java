package ru.neoflex.chmutenko.bank.CreditConveyor.util;

public class DataNotValidException extends RuntimeException{
    public DataNotValidException(String message) {
        super(message);
    }
}

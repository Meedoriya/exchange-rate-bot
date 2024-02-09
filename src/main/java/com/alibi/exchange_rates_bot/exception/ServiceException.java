package com.alibi.exchange_rates_bot.exception;

public class ServiceException extends Exception {
    public ServiceException(String message, Throwable reason) {
        super(message, reason);
    }
}

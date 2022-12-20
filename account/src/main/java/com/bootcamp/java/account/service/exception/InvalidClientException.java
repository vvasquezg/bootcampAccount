package com.bootcamp.java.account.service.exception;

public class InvalidClientException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidClientException() {
        super("Client not found");
    }
}

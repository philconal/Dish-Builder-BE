package com.conal.dishbuilder.exception;

public class BadCredentialsException extends RuntimeException{
    public BadCredentialsException(String msg){
        super(msg);
    }
}

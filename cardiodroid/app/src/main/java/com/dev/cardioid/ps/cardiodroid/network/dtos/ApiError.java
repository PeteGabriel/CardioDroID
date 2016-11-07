package com.dev.cardioid.ps.cardiodroid.network.dtos;

public class ApiError extends Exception{

    private int statusCode;
    private String message;

    public ApiError(){
        super();
    }

    public ApiError(int status, String message) {
        super(message);
        this.statusCode = status;
        this.message = message;
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
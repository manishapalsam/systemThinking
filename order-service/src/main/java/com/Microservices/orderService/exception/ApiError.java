package com.Microservices.orderService.exception;

import java.time.OffsetDateTime;
import java.util.Map;


//Machines (frontend, other services) don’t “read text”.
//        They read fields.
//
//        RFC 7807 defines a standard shape for errors.
public class ApiError {
    // A URI that identifies the error type (stable, documented)
    private String type;

    private String title;


    // HTTP status code (400, 409, 503, etc.)
    private int status;


    // Detailed explanation for THIS occurrence
    private String details;


    // API path where the error occurred
    private String instance;

    private Map<String, String> errors;

    // Timestamp helps debugging & tracing
    private OffsetDateTime timestamp;

    public ApiError() {
        this.timestamp = OffsetDateTime.now();
    }


    // Getters & setters (important for JSON serialization)

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }






}

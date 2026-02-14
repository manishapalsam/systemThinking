package com.Microservices.orderService.exception;

public class DownstreamApiError {

    private String title;
    private String details;

    public String getTitle() { return title; }
    public String getDetails() { return details; }

    public void setTitle(String title) { this.title = title; }
    public void setDetails(String details) { this.details = details; }
}

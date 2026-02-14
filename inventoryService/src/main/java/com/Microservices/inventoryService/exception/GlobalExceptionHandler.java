package com.Microservices.inventoryService.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryBusinessException.class)
    public ResponseEntity<ApiError> handleInventoryBusinessException(
            InventoryBusinessException ex,
            HttpServletRequest request){

        ApiError error = new ApiError();
        error.setType("/errors/inventory-business");
        error.setTitle(ex.getErrorCode());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setDetails(ex.getMessage());
        error.setInstance(request.getRequestURI());
        System.out.println(error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}

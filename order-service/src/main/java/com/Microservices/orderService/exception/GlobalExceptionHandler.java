package com.Microservices.orderService.exception;

import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors (@Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Field -> error message map
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(error.getField(), error.getDefaultMessage())
                );

        ApiError apiError = new ApiError();
        apiError.setType("https://oms/errors/validation");
        apiError.setTitle("Invalid Request");
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setDetails("One or more fields are invalid");
        apiError.setInstance(request.getRequestURI());
        apiError.setErrors(fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }




    //Resource not found
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFound(OrderNotFoundException ex,
                                                        HttpServletRequest request){
        ApiError apiError = new ApiError();
        apiError.setType("https://oms/errors/order-not-found");
        apiError.setTitle("OrderNot Found");
        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setDetails(ex.getMessage());
        apiError.setInstance(request.getRequestURI());

        return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }





    //business conflict
    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiError> handleBussiness(
        BusinessConflictException ex,
        HttpServletRequest request){
    ApiError apiError = new ApiError();
        apiError.setType("https://oms/errors/business");
        apiError.setTitle(ex.getErrorCode());
        apiError.setStatus(409);
        apiError.setDetails(ex.getMessage());
        apiError.setInstance(request.getRequestURI());

        return  ResponseEntity.status(409).body(apiError);
        }



        //downstream services
    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ApiError> handleDownStreamFailure(
            DownstreamServiceException ex,HttpServletRequest request
    ){
        ApiError apiError = new ApiError();
        apiError.setType("https://oms/errors/service-unavailable");
        apiError.setTitle("Service Unavailable");
        apiError.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());//503
        apiError.setDetails(ex.getMessage());
        apiError.setInstance(request.getRequestURI());


        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(apiError);
    }
}

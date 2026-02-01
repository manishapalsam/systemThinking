package com.Microservices.paymentService.controller;


import com.Microservices.paymentService.dto.PaymentRequest;
import com.Microservices.paymentService.dto.PaymentResponse;
import com.Microservices.paymentService.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest){
        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(paymentResponse);
    }

}

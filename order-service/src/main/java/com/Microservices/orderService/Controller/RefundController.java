package com.Microservices.orderService.Controller;

import com.Microservices.orderService.Service.RefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/refunds")
public class RefundController {

    private  final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<String> refund(@PathVariable("orderId") String orderId){
        refundService.refundOrder(orderId);
        return  ResponseEntity.ok("Refund Processed");
    }
}

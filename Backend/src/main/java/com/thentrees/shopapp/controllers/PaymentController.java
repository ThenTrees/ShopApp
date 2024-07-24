package com.thentrees.shopapp.controllers;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.services.vnpay.IPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final IPaymentService paymentService;

    @GetMapping("vn-pay")
    public ResponseEntity<ResponseObject> createPayment(@PathParam("id") Long orderId, HttpServletRequest req)
            throws UnsupportedEncodingException {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.createPayment(orderId, req));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<ResponseObject> payCallbackHandler(HttpServletRequest request)
            throws UnsupportedEncodingException {
        log.info("Received callback from VNPAY");
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.callback(request));
    }

    //    @GetMapping("/ipn")
    //    public ResponseEntity<ResponseObject> handleVnpayIpn(HttpServletRequest request)
    //            throws UnsupportedEncodingException {
    //        log.info("Received IPN from VNPAY");
    //        return ResponseEntity.status(HttpStatus.OK).body(paymentService.ipnPayment(request));
    //    }

    @PostMapping("/ipn")
    public ResponseEntity<ResponseObject> handleVnpayIpn(HttpServletRequest request) {
        log.info("Received IPN from VNPAY");

        //        paymentService.ipnPayment(request)
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .code(200)
                        .message("IPN successfully processed")
                        .build());
    }
}

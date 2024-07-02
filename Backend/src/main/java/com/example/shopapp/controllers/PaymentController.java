package com.example.shopapp.controllers;

import java.io.UnsupportedEncodingException;
import java.util.*;

import com.example.shopapp.dtos.responses.PaymentDTOResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.services.vnpay.IPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @GetMapping("vn-pay")
    public ResponseEntity<ResponseObject> createPayment(
            @PathParam("price") int price, @PathParam("id") Long orderId, HttpServletRequest req)
            throws UnsupportedEncodingException {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.createPayment(price, orderId, req));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<ResponseObject> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Success")
                    .code(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Failed")
                    .code(HttpStatus.OK.value())
                    .build());
        }
    }
}

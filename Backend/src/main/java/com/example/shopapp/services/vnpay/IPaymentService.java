package com.example.shopapp.services.vnpay;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;

import com.example.shopapp.dtos.responses.ResponseObject;

public interface IPaymentService {
    ResponseObject createPayment(int price, Long orderId, HttpServletRequest req)
            throws UnsupportedEncodingException;
}

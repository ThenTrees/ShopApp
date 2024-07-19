package com.thentrees.shopapp.services.vnpay;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;

import com.thentrees.shopapp.dtos.responses.ResponseObject;

public interface IPaymentService  {
        ResponseObject createPayment(Long orderId, HttpServletRequest req)
                throws UnsupportedEncodingException;

    ResponseObject callback(HttpServletRequest req) throws UnsupportedEncodingException;



    ResponseObject ipnPayment(HttpServletRequest req) throws UnsupportedEncodingException;

}

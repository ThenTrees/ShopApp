package com.thentrees.shopapp.utils;

public class RandomOTP {
    public static String generateOTP() {
        int randomPin = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomPin);
    }
}

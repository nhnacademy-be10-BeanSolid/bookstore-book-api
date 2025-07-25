package com.nhnacademy.bookapi.common.service;

import org.springframework.stereotype.Component;

@Component
public class IsbnConverter {
    public String convertIsbn10ToIsbn13(String isbn10) {
        if (isbn10 == null || isbn10.length() != 10) {
            throw new IllegalArgumentException("Invalid ISBN-10");
        }

        String isbn = "978" + isbn10.substring(0, 9);
        int sum = 0;
        for (int i = 0; i < isbn.length(); i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = 10 - (sum % 10);
        if (checksum == 10) checksum = 0;

        return isbn + checksum;
    }
}
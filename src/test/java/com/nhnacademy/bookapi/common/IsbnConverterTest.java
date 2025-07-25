package com.nhnacademy.bookapi.common;

import com.nhnacademy.bookapi.common.service.IsbnConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IsbnConverterTest {

    private final IsbnConverter converter = new IsbnConverter();

    @Test
    void convert10To13() {
        String isbn10 = "0136091814";
        String expectedIsbn13 = "9780136091813";

        String result = converter.convertIsbn10ToIsbn13(isbn10);

        assertEquals(expectedIsbn13, result);
    }

}

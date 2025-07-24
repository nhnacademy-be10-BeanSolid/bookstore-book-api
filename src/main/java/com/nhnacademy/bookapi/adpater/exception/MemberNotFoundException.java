package com.nhnacademy.bookapi.adpater.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}

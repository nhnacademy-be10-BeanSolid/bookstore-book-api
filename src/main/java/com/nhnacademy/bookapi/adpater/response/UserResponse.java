package com.nhnacademy.bookapi.adpater.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long userNo,
        String userId,
        String userPassword,
        String userName,
        String userPhoneNumber,
        String userEmail,
        LocalDate userBirth,
        int userPoint,
        @JsonProperty("auth")
        boolean isAuth,
        String userStatus,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt,
        String userGradeName
) {}
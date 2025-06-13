package com.nhnacademy.bookapi.booklike.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookLikeCreateRequest (
        @NotBlank
        @Size(max = 50)
        String userId
)
{}

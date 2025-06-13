package com.nhnacademy.bookapi.booktag.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookTagCreateRequest(
        @NotBlank
        @Size(max = 50)
        String name
) {
}

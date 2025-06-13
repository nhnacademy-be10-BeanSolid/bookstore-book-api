package com.nhnacademy.bookapi.booktag.domain.request;

import jakarta.validation.constraints.NotNull;

public record BookTagMapCreateRequest (
        @NotNull
        Long tagId
)
{}


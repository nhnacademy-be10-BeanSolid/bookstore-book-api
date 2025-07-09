package com.nhnacademy.bookapi.booktag.domain.response;

import java.util.List;

public record BookTagMapResponse(
        Long bookId,
        List<BookTagResponse> tags
)
{}
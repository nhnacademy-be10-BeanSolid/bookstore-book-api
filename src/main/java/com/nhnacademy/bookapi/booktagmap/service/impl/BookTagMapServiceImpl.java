package com.nhnacademy.bookapi.booktagmap.service.impl;

import com.nhnacademy.bookapi.booktagmap.repository.BookTagMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTagMapServiceImpl {

    private final BookTagMapRepository bookTagMapRepository;

}

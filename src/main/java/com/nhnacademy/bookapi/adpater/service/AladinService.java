package com.nhnacademy.bookapi.adpater.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.nhnacademy.bookapi.adpater.AladinAdapter;
import com.nhnacademy.bookapi.adpater.domain.AladinItem;
import com.nhnacademy.bookapi.adpater.domain.AladinSearchRequest;
import com.nhnacademy.bookapi.adpater.domain.AladinSearchResponse;
import com.nhnacademy.bookapi.common.service.IsbnConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinService {

    private final AladinAdapter aladinAdapter;
    private final ObjectMapper objectMapper;
    private final IsbnConverter isbnConverter;

    @Value("${aladin.ttbkey}")
    private String ttbKey;

    public AladinSearchResponse search(AladinSearchRequest request) throws JsonProcessingException {
        String result = aladinAdapter.searchItem(
                ttbKey,
                request.getQuery(),
                request.getStart(),
                request.getMaxResults(),
                "js"
        );
        log.info("Raw response: {}", result);
        //  \' 표준에서 인정되지 않는 이스케이프 문자
        String fixed = result.replace("\\'", "'");
        AladinSearchResponse aladinSearchResponse = objectMapper.readValue(fixed, AladinSearchResponse.class);

        List<AladinItem> fixedItems = new ArrayList<>();
        for (AladinItem item : aladinSearchResponse.item()){
            if(item.isbn13().isEmpty()) {
                String isbn13 = isbnConverter.convertIsbn10ToIsbn13(item.isbn());
                fixedItems.add(AladinItem.from(item, isbn13));
            } else {
                fixedItems.add(item);
            }
        }
        return AladinSearchResponse.from(aladinSearchResponse, fixedItems);
    }
}

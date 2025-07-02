package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryCsvFileReadService {

    private final BookCategoryRepository bookCategoryRepository;

    // 캐시(키에 해당하는 카테고리 존재하는지 DB 연결하지 않고 빠르게 확인)
    private final Map<String, BookCategory> categoryCache = new HashMap<>();

    @Transactional
    public void importCategoriesFromCsv(File csvFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            CSVParser parser = new CSVParser(br, csvFormat);

            // 한줄씩
            for (CSVRecord record : parser) {
//                long id = Long.parseLong(record.get("CID"));

                // Depth 리스트
                List<String> depths = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    String depthName = record.get(i + "Depth");
                    if (depthName != null && !depthName.isBlank()) {
                        depths.add(depthName.trim());
                    }
                }

                BookCategory parent = null;

                for (int i = 0; i < depths.size(); i++) {
                    String currentName = depths.get(i);

                    if (currentName == null || currentName.isBlank()) {
                        continue;
                    }

                    String key = (i == 0)
                            ? currentName
                            : parent.getName() + ">" + currentName;

                    BookCategory category = categoryCache.get(key);
                    if (category == null) {
                        category = bookCategoryRepository.findByNameAndParentCategory(currentName, parent).orElse(null);
                        if (category == null) {
                            category = new BookCategory(currentName, parent);
                            bookCategoryRepository.save(category);
                        }
                        categoryCache.put(key, category);
                    }

                    parent = category;
                }
            }
        }
    }
}


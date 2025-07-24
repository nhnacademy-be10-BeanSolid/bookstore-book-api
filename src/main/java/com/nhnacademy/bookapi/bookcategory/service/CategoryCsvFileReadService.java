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

    // internalId → 이름
    private final Map<Long, String> internalIdToName = new HashMap<>();
    // 이름 → BookCategory
    private final Map<String, BookCategory> nameToCategory = new HashMap<>();

    @Transactional
    public void importCategoriesFromCsv(File csvFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
            br.mark(1);
            if (br.read() != 0xFEFF) {
                br.reset();
            }

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            CSVParser parser = new CSVParser(br, csvFormat);

            for (CSVRecord record : parser) {
                List<String> depths = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    String depthName = record.get(i + "Depth");
                    if (depthName != null && !depthName.isBlank()) {
                        depths.add(depthName.trim());
                    }
                }

                Long cid = Long.parseLong(record.get("CID")); // ★ 변경: cid 읽기

                BookCategory parent = null;
                for (int i = 0; i < depths.size(); i++) {
                    String currentName = depths.get(i);
                    if (currentName == null || currentName.isBlank()) continue;

                    String key = (parent == null) ? currentName : parent.getName() + ">" + currentName;

                    BookCategory category = nameToCategory.get(key);
                    if (category == null) {
                        category = bookCategoryRepository.findByNameAndParentCategory(currentName, parent).orElse(null);
                        if (category == null) {
                            // BookCategory에 cid 생성자/필드 있다고 가정
                            category = new BookCategory(cid, currentName, parent);
                            bookCategoryRepository.save(category);

                            internalIdToName.put(cid, currentName); // ★ 변경: cid로 key 지정
                            nameToCategory.put(key, category);
                        } else {
                            internalIdToName.put(cid, currentName);
                            nameToCategory.put(key, category);
                        }
                    }
                    parent = category;
                }
            }
        }
    }

}

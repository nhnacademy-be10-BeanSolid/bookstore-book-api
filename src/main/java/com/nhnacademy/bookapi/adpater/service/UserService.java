package com.nhnacademy.bookapi.adpater.service;

import com.nhnacademy.bookapi.adpater.UserAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAdapter userAdapter;

    // 리뷰 수
    public Long countReviewsByBookId(long bookId) {
        return userAdapter.countReviewsByBookId(bookId);
    }

    // 평균 평점
    public Double getAverageEvaluationScoreByBookId(long bookId) {
        return userAdapter.getAverageEvaluationScoreByBookId(bookId);
    }
}

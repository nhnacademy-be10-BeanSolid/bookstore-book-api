package com.nhnacademy.bookapi.adpater.service;

import com.nhnacademy.bookapi.adpater.UserAdapter;
import com.nhnacademy.bookapi.adpater.exception.MemberNotFoundException;
import com.nhnacademy.bookapi.adpater.response.UserResponse;
import com.nhnacademy.bookapi.common.exception.ForbiddenException;
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

    // 유저 권한 체크
    public void getUserAuthorize(String xUserId) {
        UserResponse response = userAdapter.getUserInfo(xUserId);
        if (!response.isAuth()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
    }

    // 실제 유저인지 체크
    public void isMember(String xUserId) {
        UserResponse response = userAdapter.getUserInfo(xUserId);
        if (response == null) {
            throw new MemberNotFoundException("회원 정보를 찾을 수 없습니다.");
        }
    }
}

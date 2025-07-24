package com.nhnacademy.bookapi.adpater;

import com.nhnacademy.bookapi.adpater.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-API")
public interface UserAdapter {

    @GetMapping("/reviews/book/{bookId}/count")
    Long countReviewsByBookId(@PathVariable long bookId);

    @GetMapping("/reviews/book/{bookId}/average-score")
    Double getAverageEvaluationScoreByBookId(@PathVariable long bookId);

    @GetMapping("/users/me")
    UserResponse getUserInfo(@RequestHeader("X-USER-ID") String userId);
}
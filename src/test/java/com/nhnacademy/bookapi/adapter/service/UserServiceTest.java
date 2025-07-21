package com.nhnacademy.bookapi.adapter.service;

import com.nhnacademy.bookapi.adpater.UserAdapter;
import com.nhnacademy.bookapi.adpater.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAdapter userAdapter;

    @InjectMocks
    private UserService userService;

    @Test
    void testCountReviewsByBookId() {
        long bookId = 1L;
        long expectedCount = 5L;

        when(userAdapter.countReviewsByBookId(bookId)).thenReturn(expectedCount);

        Long actualCount = userService.countReviewsByBookId(bookId);

        assertThat(actualCount).isEqualTo(expectedCount);
    }

    @Test
    void testGetAverageEvaluationScoreByBookId() {
        long bookId = 1L;
        double expectedAverage = 4.5;

        when(userAdapter.getAverageEvaluationScoreByBookId(bookId)).thenReturn(expectedAverage);

        Double actualAverage = userService.getAverageEvaluationScoreByBookId(bookId);

        assertThat(actualAverage).isEqualTo(expectedAverage);
    }
}

package com.nhnacademy.bookapi.adapter.service;

import com.nhnacademy.bookapi.adpater.UserAdapter;
import com.nhnacademy.bookapi.adpater.exception.MemberNotFoundException;
import com.nhnacademy.bookapi.adpater.response.UserResponse;
import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.common.exception.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
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

    @Test
    void getUserAuthorize_whenUserIsAuthorized_shouldPass() {
        String xUserId = "user1";

        UserResponse mockResponse = Mockito.mock(UserResponse.class);
        when(mockResponse.isAuth()).thenReturn(true);

        when(userAdapter.getUserInfo(xUserId)).thenReturn(mockResponse);

        assertDoesNotThrow(() -> userService.getUserAuthorize(xUserId));

        verify(userAdapter).getUserInfo(xUserId);
    }

    @Test
    void getUserAuthorize_whenUserIsNotAuthorized_shouldThrowForbiddenException() {
        String xUserId = "user2";

        UserResponse mockResponse = Mockito.mock(UserResponse.class);
        when(mockResponse.isAuth()).thenReturn(false);

        when(userAdapter.getUserInfo(xUserId)).thenReturn(mockResponse);

        assertThatThrownBy(() -> userService.getUserAuthorize(xUserId))
                .isInstanceOf(ForbiddenException.class);

        verify(userAdapter).getUserInfo(xUserId);
    }

    @Test
    void isMember_shouldThrowException_whenUserNotFound() {
        String xUserId = "user123";
        when(userAdapter.getUserInfo(xUserId)).thenReturn(null);

        assertThatThrownBy(() -> userService.isMember(xUserId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void isMember_shouldNotThrowException_whenUserExists() {
        String xUserId = "user123";
        UserResponse mockResponse = Mockito.mock(UserResponse.class);
        when(userAdapter.getUserInfo(xUserId)).thenReturn(mockResponse);

        assertDoesNotThrow(() -> userService.isMember(xUserId));
    }
}

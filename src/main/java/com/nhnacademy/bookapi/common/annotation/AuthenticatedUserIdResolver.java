package com.nhnacademy.bookapi.common.annotation;

import com.nhnacademy.bookapi.common.exception.InvalidHeaderException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticatedUserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthenticatedUserId.class) != null &&
                parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String userId = webRequest.getHeader("X-USER-ID");
        if (userId == null || userId.isBlank()) {
            throw new InvalidHeaderException(InvalidHeaderException.USER_ID_BLANK);
        }
        if (userId.length() > 255) {
            throw new InvalidHeaderException(InvalidHeaderException.USER_ID_TOO_LONG);
        }
        return userId;
    }
}

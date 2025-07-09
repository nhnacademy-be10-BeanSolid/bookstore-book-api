package com.nhnacademy.bookapi.common.config;

import com.nhnacademy.bookapi.common.annotation.AuthenticatedUserIdResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthenticatedUserIdResolver userIdResolver;

    public WebConfig(AuthenticatedUserIdResolver userIdResolver) {
        this.userIdResolver = userIdResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdResolver);
    }
}

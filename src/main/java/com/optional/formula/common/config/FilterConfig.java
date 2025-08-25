package com.optional.formula.common.config;

import com.optional.formula.common.filter.JwtAuthenticationFilter;
import com.optional.formula.common.filter.JwtFilterProperties;
import com.optional.formula.common.jwt.JwtProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(
            JwtProvider jwtProvider,
            JwtFilterProperties props
    ) {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtProvider, props.getExcludePaths());

        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
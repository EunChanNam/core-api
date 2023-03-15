package com.learncha.api.common.util.logging.filter;

import com.learncha.api.common.util.logging.component.CustomRequestWrapper;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingFilter extends OncePerRequestFilter {
    private static final String LOGGING_KEY = "x-request-id";
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        MDC.put(LOGGING_KEY, requestId);
        var requestWrapper = new CustomRequestWrapper(request);
        response.addHeader(LOGGING_KEY, requestId);
        requestWrapper.setAttribute(LOGGING_KEY, requestId);
        filterChain.doFilter(requestWrapper, response);
    }
}

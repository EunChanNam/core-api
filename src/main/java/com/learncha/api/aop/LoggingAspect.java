package com.learncha.api.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.common.util.logging.component.ReqLoggingForm;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @Pointcut("execution(* com.learncha.api.auth.web.*ApiController.*(..))")
//    public void apiLoggingPointCut() {}

    @Pointcut("execution(* com.learncha.api.*.web.*ApiController.*(..))")
    public void apiLoggingPointCut() {}

    @Before(value = "apiLoggingPointCut()")
    public void reqResLogging(JoinPoint joinPoint) throws Throwable {
        var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        var parameters = getParameters(request);
        var requestId = (String) request.getAttribute("x-request-id");
        var in = request.getInputStream();
        var jsonNode = objectMapper.readTree(request.getInputStream());
        var httpMethod = request.getMethod();
        var className = joinPoint.getSignature().getDeclaringTypeName();
        var methodName = joinPoint.getSignature().getName();

        var loggingForm = ReqLoggingForm.builder()
            .requestId(requestId)
            .uri(request.getRequestURI())
            .httpMethod(httpMethod)
            .parameters(parameters)
            .className(className)
            .methodName(methodName)
            .payload(jsonNode)
            .build();

        var loggingData = objectMapper.writeValueAsString(loggingForm);

        log.info("request: {}", loggingData);
    }

    private HashMap<Object, Object> getParameters(HttpServletRequest request) {
        var parameterMap = new HashMap<>();

        for(Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            parameterMap.put(key, value);
        }

        return parameterMap;
    }

}

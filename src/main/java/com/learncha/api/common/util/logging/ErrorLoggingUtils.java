package com.learncha.api.common.util.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.common.exception.BaseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class ErrorLoggingUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void baseException(BaseException ex) throws JsonProcessingException {
        var msg = ex.getMessage(); StackTraceElement[] stackTrace = ex.getStackTrace();
        if(stackTrace.length > 0) {
            StackTraceElement top = stackTrace[0]; log.error("{}", objectMapper.writeValueAsString(
                new ErrorLoggingFormat(
                    msg,
                    String.format(
                        "%s.%s(%s:%s)",
                        top.getClassName(), top.getMethodName(), top.getFileName(), top.getLineNumber()))
                ));
        } else {
            log.error("Exception occurred: {}", ex.getMessage());
        }
    }

    public static void error(Exception ex) {
        var request_id = MDC.get("x-request-id");
        log.error("id={} {}", request_id, ex);
    }

    @Getter
    private static class ErrorLoggingFormat {
        private final String reason;
        private final String exceptionOccurredAt;

        public ErrorLoggingFormat(String reason, String exceptionOccurredAt) {
            this.reason = reason; this.exceptionOccurredAt = exceptionOccurredAt;
        }
    }

}

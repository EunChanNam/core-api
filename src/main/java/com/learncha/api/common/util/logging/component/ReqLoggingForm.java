package com.learncha.api.common.util.logging.component;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReqLoggingForm {
    private final String requestId;
    private final String uri;
    private final String httpMethod;
    private final Map<Object, Object> parameters;
    private final String className;
    private final String methodName;
    private final JsonNode payload;

    @Builder
    public ReqLoggingForm(
        String requestId,
        String uri,
        String httpMethod,
        Map<Object, Object> parameters,
        String className,
        String methodName,
        JsonNode payload
    ) {
        this.requestId = requestId;
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.parameters = parameters;
        this.className = className;
        this.methodName = methodName;
        this.payload = payload;
    }
}

package com.learncha.api.common.util.logging.component;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReqResLoggingForm {

    private final String requestId;
    private final String uri;
    private final Map<Object, Object> parameters;
    private final Object payload;

    @Builder
    public ReqResLoggingForm(String requestId, String uri, Map<Object, Object> parameters, Object payload) {
        this.requestId = requestId;
        this.uri = uri;
        this.parameters = parameters;
        this.payload = payload;
    }
}

package com.learncha.api.common.error;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ErrorResponse {
    private String timestamp;
    private int errorCode;
    private String errorMessage;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.timestamp = LocalDateTime.now().toString();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(HttpStatus httpStatus, String errorMessage) {
        return new ErrorResponse(httpStatus.value(), errorMessage);
    }

    public static ErrorResponse of(HttpStatus httpStatus, FieldError fieldError) {
        if(fieldError != null) {
            return new ErrorResponse(httpStatus.value(), fieldError.getDefaultMessage());
        }
        return new ErrorResponse(httpStatus.value(), "Invalid Param");
    }
}

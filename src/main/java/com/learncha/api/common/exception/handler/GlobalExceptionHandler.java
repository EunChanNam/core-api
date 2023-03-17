package com.learncha.api.common.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learncha.api.common.error.ErrorCode;
import com.learncha.api.common.error.ErrorResponse;
import com.learncha.api.common.exception.AlreadyAuthenticatedEmail;
import com.learncha.api.common.exception.BaseException;
import com.learncha.api.common.exception.EntityNotFoundException;
import com.learncha.api.common.exception.InvalidParamException;
import com.learncha.api.common.util.logging.ErrorLoggingUtils;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleInternalServerError(Exception ex) {
        ErrorLoggingUtils.error(ex);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.COMMON_SYSTEM_ERROR.getErrorMsg());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public ErrorResponse handleInvalidParamException(BaseException ex) throws JsonProcessingException {
        ErrorLoggingUtils.baseException(ex);
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("", ex);
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getFieldError());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        /**
         * ex
         *  error msg: isEmailAvailable.email: 이메일은 필수 값입니다. -> 이메일은 필수 값입니다.
         */
        log.error("", ex);
        String [] strings = ex.getMessage().split(": ");
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, strings[1]);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentialException(BadCredentialsException ex) {
        log.error("", ex);
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("", ex);
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // MissingRequestCookieException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestCookieException.class)
    public ErrorResponse handleMissingRequestCookieException(MissingRequestCookieException ex) {
        log.error("", ex);
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_REFRESH_TOKEN.getErrorMsg());
    }
}

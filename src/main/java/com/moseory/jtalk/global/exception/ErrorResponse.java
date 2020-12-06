package com.moseory.jtalk.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;
    private int status;
    private List<CustomFieldError> errors;
    private String code;

    /** Constructor **/
    private ErrorResponse(ErrorCode code, List<CustomFieldError> errors) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.errors = errors;
        this.code = code.getCode();
    }

    public ErrorResponse(ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>();
    }

    /**
     * Factory Method
     **/
    public static ErrorResponse of(ErrorCode code, Exception e) {
        return new ErrorResponse(code, CustomFieldError.of(e));
    }

    public static ErrorResponse of(ErrorCode code, BindingResult bindingResult) {
        return new ErrorResponse(code, CustomFieldError.of(bindingResult));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CustomFieldError {
        private String field;
        private String value;
        private String reason;

        /** Constructor **/
        private CustomFieldError(String reason) {
            this.field = "";
            this.value = "";
            this.reason = reason;
        }

        private CustomFieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        /** Factory Method **/
        private static List<CustomFieldError> of(Exception e) {
            return Collections.singletonList(new CustomFieldError(e.getMessage()));
        }

        private static List<CustomFieldError> of(BindingResult bindingResult) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            return errors.stream()
                    .map(error -> new CustomFieldError(
                            error.getField(),
                            Objects.isNull(error.getRejectedValue()) ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .collect(toList());
        }
    }
}

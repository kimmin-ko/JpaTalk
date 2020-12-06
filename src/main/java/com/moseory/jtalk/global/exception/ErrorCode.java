package com.moseory.jtalk.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "Invalid input value"),

    // Member
    DUPLICATE_EMAIL(400, "M001", "This email is a duplicate."),
    DUPLICATE_ACCOUNT(400, "M002", "This account is a duplicate.")


    ;

    private final int status;
    private final String code;
    private final String message;

}

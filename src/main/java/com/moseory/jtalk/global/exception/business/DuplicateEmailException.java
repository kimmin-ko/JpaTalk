package com.moseory.jtalk.global.exception.business;

import com.moseory.jtalk.global.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(String message) {
        super(message, ErrorCode.DUPLICATE_EMAIL);
    }

}

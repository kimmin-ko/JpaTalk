package com.moseory.jtalk.global.exception.business;

import com.moseory.jtalk.global.exception.ErrorCode;

public class DuplicateAccountException extends BusinessException {
    public DuplicateAccountException(String message) {
        super(message, ErrorCode.DUPLICATE_ACCOUNT);
    }
}

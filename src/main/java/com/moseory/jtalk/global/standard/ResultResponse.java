package com.moseory.jtalk.global.standard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResultResponse<T> {

    private int status;
    private String message;
    private T data;

    @Builder
    public ResultResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
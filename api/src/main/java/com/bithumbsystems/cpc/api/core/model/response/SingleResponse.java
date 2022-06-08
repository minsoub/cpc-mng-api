package com.bithumbsystems.cpc.api.core.model.response;

import com.bithumbsystems.cpc.api.core.model.enums.ReturnCode;
import lombok.Getter;

@Getter
public class SingleResponse<T> {
    private final ReturnCode result;
    private T data;

    public SingleResponse(T data) {
        this.result = ReturnCode.SUCCESS;
        this.data = data;
    }

    public SingleResponse() {
        this.result = ReturnCode.SUCCESS;
    }
}

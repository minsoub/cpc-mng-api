package com.bithumbsystems.cpc.management.api.core.model.response;

import com.bithumbsystems.cpc.management.api.core.model.enums.ReturnCode;
import lombok.Getter;

@Getter
public class SingleResponse<T> {
    private final ReturnCode result;
    private final T data;

    public SingleResponse(T data) {
        this.result = ReturnCode.SUCCESS;
        this.data = data;
    }
}

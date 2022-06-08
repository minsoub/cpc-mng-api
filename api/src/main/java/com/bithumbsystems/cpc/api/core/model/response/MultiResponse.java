package com.bithumbsystems.cpc.api.core.model.response;

import com.bithumbsystems.cpc.api.core.model.enums.ReturnCode;
import java.util.List;
import lombok.Getter;

@Getter
public class MultiResponse<T> {
    private final ReturnCode result;
    private final List<T> data;

    public MultiResponse(List<T> data) {
        this.result = ReturnCode.SUCCESS;
        this.data = data;
    }
}

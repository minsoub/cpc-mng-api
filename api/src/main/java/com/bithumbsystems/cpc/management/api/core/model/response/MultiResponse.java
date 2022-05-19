package com.bithumbsystems.cpc.management.api.core.model.response;

import com.bithumbsystems.cpc.management.api.core.model.enums.ReturnCode;
import java.util.List;
import lombok.Getter;

@Getter
public class MultiResponse<T> {
    private final ReturnCode result;
    private final List<T> data;

    MultiResponse(List<T> data) {
        this.result = ReturnCode.SUCCESS;
        this.data = data;
    }
}

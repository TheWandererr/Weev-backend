package com.pivo.weev.backend.rest.model.request;

import com.pivo.weev.backend.rest.model.common.PeriodRest;
import java.time.temporal.Temporal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeriodRequest<T extends PeriodRest<? extends Temporal>> {

    private T period;
}

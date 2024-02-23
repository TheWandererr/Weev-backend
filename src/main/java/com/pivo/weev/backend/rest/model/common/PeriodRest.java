package com.pivo.weev.backend.rest.model.common;

import java.time.temporal.Temporal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeriodRest<T extends Temporal> {

    private T start;
    private T end;
}

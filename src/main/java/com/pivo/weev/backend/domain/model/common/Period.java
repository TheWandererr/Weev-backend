package com.pivo.weev.backend.domain.model.common;

import java.time.temporal.Temporal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Period<T extends Temporal> {

    private T start;
    private T end;
}

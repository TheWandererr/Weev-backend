package com.pivo.weev.backend.domain.model.common;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Modifiable {

    private Instant createdAt;
}

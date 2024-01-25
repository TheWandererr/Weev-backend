package com.pivo.weev.backend.config.async;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutorProperties {

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private String prefix;
}

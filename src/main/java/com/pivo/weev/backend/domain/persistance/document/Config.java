package com.pivo.weev.backend.domain.persistance.document;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Config {

    private Map<String, Object> map;
    private Integer integer;
    private Double vDouble;
    private String string;

    public Map<String, Object> getMap() {
        if (isNull(map)) {
            map = new HashMap<>();
        }
        return map;
    }
}
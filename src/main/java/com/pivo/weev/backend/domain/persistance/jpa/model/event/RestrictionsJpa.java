package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class RestrictionsJpa {

    @Column
    private Boolean joinAfterStartDisallowed;
    @Column
    private Boolean joinByRequest;
}

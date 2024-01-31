package com.pivo.weev.backend.domain.persistance.jpa.model.meet;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.validator.constraints.Length;

@Embeddable
@Check(constraints = "amount is null or fee > 0")
@Getter
@Setter
public class EntryFeeJpa {

    @Column
    private BigDecimal amount;
    @Length(max = 10)
    @Column(length = 10)
    private String currency;
}
package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "event_relevance")
@SequenceGenerator(sequenceName = "event_relevance_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RelevanceJpa extends SequencedPersistable<Long> {

    @Basic(fetch = FetchType.LAZY)
    @Formula(value = "abs(extract(epoch from (utc_start_date_time - current_timestamp)))")
    private BigDecimal weight;
    @Basic(fetch = FetchType.LAZY)
    @Formula(value = "case when extract(epoch from (utc_end_date_time - current_timestamp)) < 0 then 1 else 0 end")
    private Integer past;
}

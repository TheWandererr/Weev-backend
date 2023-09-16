package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_SUBCATEGORY_NAME;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event_subcategories")
@SequenceGenerator(sequenceName = "event_subcategories_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class SubcategoryJpa extends SequencedPersistable<Long> {

    @Column(name = EVENT_SUBCATEGORY_NAME, unique = true)
    private String name;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryJpa category;
}

package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_CATEGORY_NAME;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event_categories")
@SequenceGenerator(sequenceName = "event_category_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class CategoryJpa extends SequencedPersistable<Long> {

    @Column(name = EVENT_CATEGORY_NAME, unique = true)
    private String name;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubcategoryJpa> subcategories;

    @Override
    public String toString() {
        return name;
    }
}

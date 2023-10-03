package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static jakarta.persistence.CascadeType.ALL;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@SequenceGenerator(sequenceName = "role_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@EqualsAndHashCode(of = "name", callSuper = true)
public class UserRoleJpa extends SequencedPersistable<Long> {

    @Column
    private String name;
    @OneToMany(fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "role")
    private Set<AuthorityJpa> authorities;

    public Set<AuthorityJpa> getAuthorities() {
        if (isNull(authorities)) {
            authorities = new HashSet<>();
        }
        return authorities;
    }
}

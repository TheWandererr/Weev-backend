package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static jakarta.persistence.CascadeType.ALL;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "roles")
@SequenceGenerator(sequenceName = "role_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class UserRoleJpa extends SequencedPersistable<Long> {

    @Column(unique = true)
    private String name;
    @OneToMany(fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "role")
    private Set<AuthorityJpa> authorities;

    public Set<AuthorityJpa> getAuthorities() {
        if (isNull(authorities)) {
            authorities = new HashSet<>();
        }
        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy oProxy
                ? oProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        UserRoleJpa that = (UserRoleJpa) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}

package com.pivo.weev.backend.domain.persistance.jpa.model.meet;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Discriminators.MEET_JOIN;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(MEET_JOIN)
public class MeetJoinRequestJpa extends MeetRequestJpa {

    @OneToOne(cascade = {MERGE, PERSIST})
    @JoinColumn(name = "user_id")
    private UserJpa user;

    public MeetJoinRequestJpa(MeetJpa meet, UserJpa user, Instant expiresAt) {
        super(meet, expiresAt);
        this.user = user;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        Class<?> oEffectiveClass = other instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : other.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy thisHibernateProxy ? thisHibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        MeetJoinRequestJpa that = (MeetJoinRequestJpa) other;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}

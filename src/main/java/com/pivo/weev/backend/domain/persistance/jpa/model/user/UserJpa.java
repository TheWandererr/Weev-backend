package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_EMAIL;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_NAME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_PASSWORD;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_PHONE_NUMBER;
import static jakarta.persistence.CascadeType.ALL;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ModifiableJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Check(constraints = "(email is not null or phone_number is not null) and nickname <> email")
@SequenceGenerator(sequenceName = "client_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class UserJpa extends ModifiableJpa<Long> {

    @NotBlank
    @Length(max = 120)
    @Column(length = 120, name = USER_NAME, nullable = false)
    private String name;
    @NotBlank
    @Length(max = 20)
    @Column(length = 20, name = USER_NICKNAME, unique = true, nullable = false)
    private String nickname;
    @NotBlank
    @Column(name = USER_PASSWORD, nullable = false)
    private String password;
    @Length(max = 80)
    @Column(length = 80, name = USER_EMAIL, unique = true)
    private String email;
    @Length(max = 20)
    @Column(length = 20, name = USER_PHONE_NUMBER, unique = true)
    private String phoneNumber;
    @OneToOne(cascade = ALL)
    @JoinColumn(name = "role_id")
    private UserRoleJpa role;
    private Boolean active;
}

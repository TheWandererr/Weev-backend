package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;


import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.equal;
import static com.pivo.weev.backend.domain.persistance.utils.SpecificationUtils.fieldPathFrom;
import static java.util.List.of;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SpecificationBuilder;
import jakarta.persistence.metamodel.Attribute;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class UserSpecificationBuilder {

    public enum UsernameType {
        PHONE_NUMBER,
        NICKNAME,
        EMAIL,
        ANY
    }

    public static Specification<UserJpa> buildUserSearchSpecification(String username, UsernameType usernameType) {
        if (isBlank(username)) {
            return SimpleSpecifications.empty();
        }
        if (usernameType == ANY) {
            return buildUserSearchSpecification(username);
        }
        Attribute<UserJpa, String> attribute = switch (usernameType) {
            case PHONE_NUMBER -> UserJpa_.phoneNumber;
            case NICKNAME -> UserJpa_.nickname;
            case EMAIL -> UserJpa_.email;
            default -> throw new IllegalArgumentException();
        };
        SpecificationBuilder<UserJpa> specificationBuilder = new SpecificationBuilder<>();
        return specificationBuilder.andEqual(fieldPathFrom(attribute), username, String.class).build();
    }

    private static Specification<UserJpa> buildUserSearchSpecification(String nicknameOrEmailOrPhoneNumber) {
        return buildUserSearchSpecification(nicknameOrEmailOrPhoneNumber, nicknameOrEmailOrPhoneNumber, nicknameOrEmailOrPhoneNumber);
    }

    public static Specification<UserJpa> buildUserSearchSpecification(String nickname, String email, String phoneNumber) {
        SpecificationBuilder<UserJpa> specificationBuilder = new SpecificationBuilder<>();
        List<Specification<UserJpa>> specifications = of(equal(UserJpa_.nickname, nickname, String.class),
                                                         equal(UserJpa_.email, email, String.class),
                                                         equal(UserJpa_.phoneNumber, phoneNumber, String.class));
        return specificationBuilder.orAny(specifications).build();
    }
}

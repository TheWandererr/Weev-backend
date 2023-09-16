package com.pivo.weev.backend.jpa.specification.builder;


import static com.pivo.weev.backend.jpa.specification.builder.UserJpaSpecificationBuilder.UsernameType.ANY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.pivo.weev.backend.jpa.model.user.UserJpa;
import com.pivo.weev.backend.jpa.specification.engine.specification.SimpleSpecifications;
import com.pivo.weev.backend.jpa.specification.engine.specification.SpecificationBuilder;
import com.pivo.weev.backend.jpa.utils.Constants.Paths;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class UserJpaSpecificationBuilder {

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
        String fieldPath = switch (usernameType) {
            case PHONE_NUMBER -> Paths.USER_PHONE_NUMBER;
            case NICKNAME -> Paths.USER_NICKNAME;
            case EMAIL -> Paths.USER_EMAIL;
            default -> throw new IllegalArgumentException();
        };
        SpecificationBuilder<UserJpa> specificationBuilder = new SpecificationBuilder<>();
        return specificationBuilder.andEqual(fieldPath, username, String.class).build();
    }

    private static Specification<UserJpa> buildUserSearchSpecification(String nicknameOrEmailOrPhoneNumber) {
        return buildUserSearchSpecification(nicknameOrEmailOrPhoneNumber, nicknameOrEmailOrPhoneNumber, nicknameOrEmailOrPhoneNumber);
    }

    @SuppressWarnings("unchecked")
    public static Specification<UserJpa> buildUserSearchSpecification(String nickname, String email, String phoneNumber) {
        SpecificationBuilder<UserJpa> specificationBuilder = new SpecificationBuilder<>();
        return specificationBuilder.orAny(
                SimpleSpecifications.equal(Paths.USER_NICKNAME, nickname, String.class),
                SimpleSpecifications.equal(Paths.USER_EMAIL, email, String.class),
                SimpleSpecifications.equal(Paths.USER_PHONE_NUMBER, phoneNumber, String.class)
        ).build();
    }
}

package com.pivo.weev.backend.domain.model.user;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.rest.mapping.UsernameFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Contacts {

    private String email;
    private String phoneNumber;

    public Contacts(String email, String phoneNumber) {
        this.email = getMapper(UsernameFormatter.class).formatUsername(email);
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = getMapper(UsernameFormatter.class).formatUsername(email);
    }

    public boolean hasEmail() {
        return isNotBlank(email);
    }

    public boolean hasPhoneNumber() {
        return isNotBlank(phoneNumber);
    }
}

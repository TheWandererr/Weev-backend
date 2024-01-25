package com.pivo.weev.backend.domain.model.user;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.rest.mapping.UsernameFormatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contacts {

    private String email;
    private String phoneNumber;

    public Contacts(String email, String phoneNumber) {
        this.email = getMapper(UsernameFormatter.class).formatUsername(email);
        this.phoneNumber = phoneNumber;
    }
}

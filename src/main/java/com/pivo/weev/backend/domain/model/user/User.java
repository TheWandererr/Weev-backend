package com.pivo.weev.backend.domain.model.user;

import com.pivo.weev.backend.domain.model.common.Identifiable;
import com.pivo.weev.backend.domain.model.common.Image;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class User extends Identifiable {

    private String name;
    private String nickname;
    private Contacts contacts;
    private Image avatar;

}

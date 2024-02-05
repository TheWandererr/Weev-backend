package com.pivo.weev.backend.domain.model.user;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.pivo.weev.backend.domain.model.common.Identifiable;
import com.pivo.weev.backend.domain.model.common.Image;
import java.util.ArrayList;
import java.util.List;
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
    private int participatedMeets;
    private int createdMeets;
    private boolean deleted;
    private List<Device> devices;

    public static User deleted() {
        User user = new User();
        user.setDeleted(true);
        return user;
    }

    public List<Device> getDevices() {
        if (isNull(devices)) {
            devices = new ArrayList<>();
        }
        return devices;
    }

    public boolean hasName() {
        return isNotBlank(name);
    }

    public boolean hasNickname() {
        return isNotBlank(nickname);
    }
}

package com.pivo.weev.backend.domain.model.meet;

import com.pivo.weev.backend.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetJoinRequest extends MeetRequest {

    private User joiner;
}

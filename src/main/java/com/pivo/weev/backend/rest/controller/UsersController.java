package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;

import com.pivo.weev.backend.domain.service.user.UsersService;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.NicknameAvailabilityResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/nickname/availability")
    public BaseResponse isNicknameAvailable(@RequestParam @NotBlank(message = MUST_BE_NOT_BLANK_ERROR) String nickname) {
        boolean nicknameAvailable = usersService.isNicknameAvailable(nickname.toLowerCase());
        return new NicknameAvailabilityResponse(nicknameAvailable);
    }

}

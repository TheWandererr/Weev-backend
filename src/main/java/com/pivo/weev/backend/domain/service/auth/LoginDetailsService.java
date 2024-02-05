package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getCurrentRequest;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHENTICATION_DENIED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.CREDENTIALS_ERROR;

import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.service.user.UsersService;
import com.pivo.weev.backend.rest.utils.HttpServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginDetailsService implements UserDetailsService {

    private final UsersService usersService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest httpServletRequest = getCurrentRequest();
        String deviceId = getDeviceId(httpServletRequest);
        return usersService.findUserJpa(username)
                           .map(userJpa -> LoginDetails.from(userJpa, username.toLowerCase(), deviceId, httpServletRequest.getRequestURI()))
                           .orElseThrow(() -> new UsernameNotFoundException(CREDENTIALS_ERROR));
    }

    private String getDeviceId(HttpServletRequest httpServletRequest) {
        return HttpServletUtils.getDeviceId(httpServletRequest)
                               .orElseThrow(() -> new InternalAuthenticationServiceException(AUTHENTICATION_DENIED));
    }
}

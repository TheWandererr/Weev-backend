package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getCurrentRequest;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHENTICATION_DENIED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.CREDENTIALS_ERROR;

import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.model.exception.AuthenticationDeniedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.user.DeviceService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.rest.utils.HttpServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginDetailsService implements UserDetailsService {

    private final UserResourceService userResourceService;
    private final DeviceService deviceService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest httpServletRequest = getCurrentRequest();
        String deviceId = getDeviceId(httpServletRequest);
        UserJpa user = userResourceService.findUserJpa(username).orElseThrow(() -> new UsernameNotFoundException(CREDENTIALS_ERROR));
        DeviceJpa device = deviceService.resolveDevice(user, deviceId);
        return LoginDetails.from(user, device, username.toLowerCase(), httpServletRequest.getRequestURI());
    }

    private String getDeviceId(HttpServletRequest httpServletRequest) {
        return HttpServletUtils.getDeviceId(httpServletRequest)
                               .orElseThrow(() -> new AuthenticationDeniedException(AUTHENTICATION_DENIED));
    }
}

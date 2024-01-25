package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;
import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getCurrentRequest;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.CREDENTIALS_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PERMISSIONS_ERROR;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import com.pivo.weev.backend.rest.utils.HttpServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginDetailsService implements UserDetailsService {

    private final UserRepositoryWrapper userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest httpServletRequest = getCurrentRequest();
        String deviceId = getDeviceId(httpServletRequest);
        String formattedUsername = ofNullable(username).map(String::toLowerCase).orElse(EMPTY);
        Specification<UserJpa> specification = buildUserSearchSpecification(formattedUsername, ANY);
        return userRepository.find(specification)
                             .map(userJpa -> LoginDetails.from(userJpa, formattedUsername, deviceId, httpServletRequest.getRequestURI()))
                             .orElseThrow(() -> new UsernameNotFoundException(CREDENTIALS_ERROR + TITLE));
    }

    private String getDeviceId(HttpServletRequest httpServletRequest) {
        return HttpServletUtils.getDeviceId(httpServletRequest)
                               .orElseThrow(() -> new UsernameNotFoundException(PERMISSIONS_ERROR + TITLE));
    }
}

package com.pivo.weev.backend.rest.service;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserJpaSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserJpaSpecificationBuilder.buildUserSearchSpecification;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.AUTHENTICATION_FAILED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.MISSING_COOKIE;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getCurrentRequest;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
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
                             .orElseThrow(() -> new UsernameNotFoundException(AUTHENTICATION_FAILED));
    }

    private String getDeviceId(HttpServletRequest httpServletRequest) {
        return HttpServletUtils.getDeviceId(httpServletRequest)
                               .orElseThrow(() -> new UsernameNotFoundException(MISSING_COOKIE));
    }
}

package com.pivo.weev.backend.domain.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
}

package com.pivo.weev.backend.rest.aspect;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class ResourceOwnerVerifier {

    @Before("@annotation(com.pivo.weev.backend.rest.annotation.ResourceOwner)")
    public void verify(JoinPoint jp) {
        Long userId = (Long) jp.getArgs()[0];
        if (!Objects.equals(userId, getUserId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }
}

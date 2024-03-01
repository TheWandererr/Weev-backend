package com.pivo.weev.backend.integration.firebase.client;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance;
import static java.util.Collections.emptyMap;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.Parameter;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RemoteConfigClient {

    private final FirebaseRemoteConfig api;
    private final ApplicationLoggingHelper loggingHelper;

    public RemoteConfigClient(FirebaseApplication application, ApplicationLoggingHelper loggingHelper) {
        this.api = getInstance(application.getInstance());
        this.loggingHelper = loggingHelper;
    }

    public Parameter getParameter(String key) {
        return getParameters().get(key);
    }

    public Map<String, Parameter> getParameters() {
        try {
            return api.getTemplate().getParameters();
        } catch (FirebaseRemoteConfigException exception) {
            log.error(loggingHelper.buildLoggingError(exception, null, false));
            return emptyMap();
        }
    }
}

package com.pivo.weev.backend.integration.firebase.factory;

import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static java.util.stream.Collectors.toMap;

import com.google.gson.Gson;
import com.pivo.weev.backend.config.messaging.MessageBundle;
import com.pivo.weev.backend.integration.firebase.model.notification.PushNotificationMessage;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationsFactory {

    private final MessageBundle messageBundle;
    private final Gson gson;

    public PushNotificationMessage build(String titleCode,
                                         Object[] titleArgs,
                                         String bodyCode,
                                         Object[] bodyArgs,
                                         Map<String, Object> details,
                                         Set<String> notificationTokens,
                                         Locale locale) {
        Map<String, String> data = collect(details.entrySet(), toMap(Entry::getKey, entry -> gson.toJson(entry.getValue())));
        String title = messageBundle.getPushNotificationMessage(titleCode, locale, titleArgs);
        String body = messageBundle.getPushNotificationMessage(bodyCode, locale, bodyArgs);
        return new PushNotificationMessage(title, body, data, notificationTokens);
    }
}

package com.pivo.weev.backend.websocket.utils;

import static com.pivo.weev.backend.websocket.utils.Constants.BrokerDestinations.TOPIC_BROKER_DESTINATION;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String STOMP_ENDPOINT = "/ws";

    @UtilityClass
    public static final class Headers {

        public static final String SIMP_DESTINATION = "simpDestination";
    }

    @UtilityClass
    public static final class SubscriptionDestinations {

        public static final String UPDATES = "/user" + TOPIC_BROKER_DESTINATION + "/updates";
        public static final String CHAT = TOPIC_BROKER_DESTINATION + "/chats";
    }

    @UtilityClass
    public static final class UserDestinations {

        public static final String UPDATES = TOPIC_BROKER_DESTINATION + "/updates";

        public static final String CHAT_PATTERN = TOPIC_BROKER_DESTINATION + "/chats.%s";

    }

    @UtilityClass
    public static final class BrokerDestinations {

        public static final String TOPIC_BROKER_DESTINATION = "/topic";
    }

    @UtilityClass
    public static class ApplicationDestinations {

        public static final String APPLICATION_DESTINATION = "/app";

    }

    @UtilityClass
    public static final class MessageTypes {

        public static final String MESSAGE = "MESSAGE";
        public static final String EVENT = "EVENT";
    }

    @UtilityClass
    public static final class MessageEvents {

        public static final String SUBSCRIBED = "SUBSCRIBED";
    }

    @UtilityClass
    public static final class PayloadKeys {

        public static final String CHAT = "chat";
    }
}

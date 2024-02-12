/*
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.PathVariables.CHAT_ID;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.TOPIC_DESTINATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.Paths;
import com.pivo.weev.backend.websocket.model.ChatWs;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class WebSocketConfigurationTest {

    private String URL;
    private WebSocketStompClient stompClient;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        URL = "ws://localhost:8080/ws";
        stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new StringMessageConverter());
        objectMapper = new ObjectMapper();
    }

    @Test
    public void should_connect_successfully() throws IOException, InterruptedException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);
        StompSessionHandler handler = new StompSessionHandler() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("Connected");
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                log.error(exception.getMessage());
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error(exception.getMessage());
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(payload);
                blockingQueue.add((String) payload);
            }
        };
        CompletableFuture<StompSession> futureSession = stompClient.connectAsync(URL, handler);

        ChatWs payload = new ChatWs();
        payload.setId(1L);

        StompSession session = futureSession.join();

        String destinationSubscribe = TOPIC_DESTINATION + Paths.CHAT_MESSAGES.replace(CHAT_ID, payload.getId().toString());
        session.subscribe(destinationSubscribe, handler);

        String destinationSend = TOPIC_DESTINATION + Paths.CHAT_CREATED;

        session.send(destinationSend, payload.getId().toString());

        Thread.sleep(3000);

        String message = blockingQueue.poll();
        System.out.println(message);

    }


}
*/

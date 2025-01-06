package ru.itmo.marketplace.service.review.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfiguration {

    private final DiscoveryClient discoveryClient;

    @Bean
    public StompSession wsStompSession() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};

        List<ServiceInstance> instances = discoveryClient.getInstances("notification-service");
        String url = "ws://" + instances.get(0).getHost() + ":" + instances.get(0).getPort() + "/ws-server";
        CompletableFuture<StompSession> stompSessionCompletableFuture = stompClient.connectAsync(url, sessionHandler);
        return stompSessionCompletableFuture.join();
    }

}

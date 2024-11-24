package ru.itmo.modules.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class UserFeignClientInterceptor implements ReactiveHttpRequestInterceptor {
    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication != null && authentication.isAuthenticated())
                .cast(InternalAuthentication.class)
                .map(authentication -> {
                    request.headers().put("X-User-Id", Collections.singletonList(authentication.getUserId().toString()));
                    request.headers().put("X-User-Role", Collections.singletonList(authentication.getUserRole()));
                    return request;
                })
                .switchIfEmpty(Mono.just(request));
    }
}

package ru.itmo.service.gateway.security;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.itmo.common.dto.user.ValidateTokenRequestDto;
import ru.itmo.common.exception.InvalidTokenException;
import ru.itmo.marketplace.service.authentication.client.AuthenticationServiceClient;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Lazy
    @Autowired
    private AuthenticationServiceClient authenticationServiceClient;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.error("Missing authorization header");
                    throw new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Missing authorization header"
                    );
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                String token = authHeader;
                return Mono.fromCallable(() -> authenticationServiceClient.validate(
                                new ValidateTokenRequestDto(token)
                        ))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(user -> {
                            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Id", user.getUserId().toString())
                                    .header("X-User-Role", user.getUserRoleDto())
                                    .build();

                            ServerWebExchange modifiedExchange = exchange;
                            modifiedExchange = exchange.mutate()
                                    .request(modifiedRequest)
                                    .build();

                            return chain.filter(modifiedExchange);
                        })
                        .onErrorResume(e -> {
                            log.error("Invalid token", e);
                            if (e.getCause() instanceof FeignException.FeignClientException ex && is4xx(ex)) {
                                return Mono.error(new InvalidTokenException(
                                        "Provided authentication token is invalid or expired"
                                ));
                            }
                            return Mono.error(e);
                        });
            }
            return chain.filter(exchange);
        };
    }

    private static boolean is4xx(FeignException.FeignClientException ex) {
        return ex.status() >= 400 && ex.status() <= 499;
    }

    public static class Config {
    }
}

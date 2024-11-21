package ru.itmo.service.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

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

                String finalAuthHeader = authHeader;
                return Mono.fromCallable(() -> jwtUtil.extractUserAndValidate(finalAuthHeader))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(user -> {
                            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Id", user.getId().toString())
                                    .header("X-User-Name", user.getUsername())
                                    .header("X-User-Role", user.getRole())
                                    .build();

                            ServerWebExchange modifiedExchange = exchange;
                            modifiedExchange = exchange.mutate()
                                    .request(modifiedRequest)
                                    .build();

                            return chain.filter(modifiedExchange);
                        })
                        .onErrorResume(e -> {
                            log.error("Invalid token", e);
                            return Mono.error(new ResponseStatusException(
                                    HttpStatus.UNAUTHORIZED,
                                    "Provided authentication token is invalid or expired"
                            ));
                        });
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}

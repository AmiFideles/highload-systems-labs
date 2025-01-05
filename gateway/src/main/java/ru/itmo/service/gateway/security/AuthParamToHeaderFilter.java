package ru.itmo.service.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthParamToHeaderFilter extends AbstractGatewayFilterFactory<AuthParamToHeaderFilter.Config> {

    public AuthParamToHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            String authParam = exchange.getRequest().getQueryParams().getFirst("auth");

            if (authParam != null) {
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("Authorization", authParam)
                        .build();

                exchange = exchange.mutate().request(request).build();
            }

            return chain.filter(exchange);
        }, OrderedGatewayFilter.HIGHEST_PRECEDENCE);
    }

    public static class Config {
    }
}

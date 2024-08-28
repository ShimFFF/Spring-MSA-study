package com.example.gatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) { //해당 필터는 application.yml에 설정해준다.

        //OrderedGatewayFilter: 필터의 우선순위를 지정할 수 있는 클래스
        // OrderedGatewayFilter.HIGHEST_PRECEDENCE: 가장 높은 우선순위를 가진 필터
        // 로깅 필터이기 때문에, 가장 먼저 실행되어야 함
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging PRE filter: baseMessage -> {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Logging PRE filter request id -> {}", request.getId());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging POST filter: response code -> {}", response.getStatusCode());
                }
            }));
        }, OrderedGatewayFilter.HIGHEST_PRECEDENCE);
    }


    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}

package com.example.gatewayservice.filter;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        OrderedGatewayFilter orderedGatewayFilter = new OrderedGatewayFilter((exchange, chain) -> {
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

        return orderedGatewayFilter;
    }


    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}

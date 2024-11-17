package bg.fibank.cmsgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayTimingFilter {

    private static final Logger logger = LoggerFactory.getLogger(GatewayTimingFilter.class);

    @Bean
    public GlobalFilter timingFilter() {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - startTime;
                logger.info("Request {} took {} ms", exchange.getRequest().getURI(), duration);
            }));
        };
    }
}

package bg.fibank.cmsgateway.filter;

import bg.fibank.cmsgateway.repository.TokenRepository;
import bg.fibank.cmsgateway.util.LoginTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenValidationFilter extends AbstractGatewayFilterFactory<Object> {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String X_FIB_LOGIN_HEADER = "x-fib-login";
    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    public TokenValidationFilter(TokenRepository tokenRepository, ObjectMapper objectMapper) {
        super(Object.class);
        this.tokenRepository = tokenRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String authorization = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
            String xFibLogin = exchange.getRequest().getHeaders().getFirst(X_FIB_LOGIN_HEADER);

            // Validate Authorization token
            if (authorization == null || authorization.isEmpty() || !authorization.matches(UUID_REGEX)) {
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid authorization token");
            }

            // Validate x-fib-login token
            if (xFibLogin == null || xFibLogin.isEmpty() || !LoginTokenUtil.verifyLoginToken(xFibLogin, authorization)) {
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid login token");
            }

            // Validate user metadata asynchronously
            return Mono.fromCallable(() -> tokenRepository.getUserMetadata(authorization))
                    .flatMap(metadata -> {
                        if (metadata == null) {
                            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "User metadata not found");
                        }

                        try {
                            String metadataJson = objectMapper.writeValueAsString(metadata);

                            return chain.filter(
                                    exchange.mutate()
                                            .request(exchange.getRequest().mutate()
                                                    .header("x-meta", metadataJson)
                                                    .build())
                                            .build()
                            );
                        } catch (Exception e) {
                            return sendErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
                        }
                    })
                    .onErrorResume(e -> sendErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
        };
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", String.valueOf(status.value()));

        try {
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory()
                    .wrap(objectMapper.writeValueAsBytes(errorResponse))));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

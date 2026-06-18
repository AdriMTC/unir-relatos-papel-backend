package com.unir.gateway.cloud.decorator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.cloud.model.GatewayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestDecoratorFactory {
    private final ObjectMapper objectMapper;

    public ServerHttpRequestDecorator getDecorator(GatewayRequest request) {

        return switch (request.getTargetMethod().name().toUpperCase()) {
            case "GET" -> new GetRequestDecorator(request);
            case "POST", "PUT", "PATCH", "DELETE" ->
                    new GenericBodyRequestDecorator(request, objectMapper);
            default -> throw new IllegalArgumentException("Unsupported method");
        };
    }
}

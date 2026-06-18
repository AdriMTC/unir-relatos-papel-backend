package com.unir.gateway.cloud.decorator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.cloud.model.GatewayRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

public class GenericBodyRequestDecorator extends ServerHttpRequestDecorator {
    private final GatewayRequest gatewayRequest;
    private final ObjectMapper objectMapper;

    public GenericBodyRequestDecorator(GatewayRequest request, ObjectMapper mapper) {
        super(request.getExchange().getRequest());
        this.gatewayRequest = request;
        this.objectMapper = mapper;
    }

    @Override
    @NonNull
    public HttpMethod getMethod() {
        return gatewayRequest.getTargetMethod();
    }

    @Override
    @NonNull
    public URI getURI() {
        return UriComponentsBuilder
                .fromUri((URI) gatewayRequest.getExchange()
                        .getAttributes()
                        .get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR))
                .build()
                .toUri();
    }

    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return gatewayRequest.getHeaders();
    }

    @Override
    @NonNull
    @SneakyThrows
    public Flux<DataBuffer> getBody() {
        DataBufferFactory factory = new DefaultDataBufferFactory();
        byte[] body = objectMapper.writeValueAsBytes(gatewayRequest.getBody());
        DataBuffer buffer = factory.wrap(body);
        return Flux.just(buffer);
    }
}

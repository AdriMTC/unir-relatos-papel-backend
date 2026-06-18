package com.unir.gateway.cloud.filter;

import com.unir.gateway.cloud.decorator.RequestDecoratorFactory;
import com.unir.gateway.cloud.model.GatewayRequest;
import com.unir.gateway.cloud.utils.RequestBodyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestTranslationFilter implements GlobalFilter {
    private final RequestBodyExtractor extractor;
    private final RequestDecoratorFactory factory;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (!HttpMethod.POST.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(buffer -> {

                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);

                    String rawBody = new String(bytes, StandardCharsets.UTF_8);

                    if (!rawBody.contains("targetMethod")) {
                        DataBuffer passthroughBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        ServerHttpRequestDecorator passthrough = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return Flux.just(passthroughBuffer);
                            }
                        };
                        return chain.filter(exchange.mutate().request(passthrough).build());
                    }

                    DataBuffer newBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    GatewayRequest request = extractor.getRequest(exchange, newBuffer);
                    var decoratedRequest = factory.getDecorator(request);

                    exchange.getAttributes().put(
                            ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
                            decoratedRequest.getURI()
                    );

                    log.info("Transformed {} → {}", exchange.getRequest().getMethod(), request.getTargetMethod());

                    return chain.filter(exchange.mutate().request(decoratedRequest).build());
                });
    }
}

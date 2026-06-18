package com.unir.gateway.cloud.filter;

import com.unir.gateway.cloud.decorator.RequestDecoratorFactory;
import com.unir.gateway.cloud.model.GatewayRequest;
import com.unir.gateway.cloud.utils.RequestBodyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

                    GatewayRequest request = extractor.getRequest(exchange, buffer);

                    if (request.getTargetMethod() == null) {
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().setComplete();
                    }

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

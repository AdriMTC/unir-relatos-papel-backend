package com.unir.gateway.cloud.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhantomTokenFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
            return chain.filter(exchange);
        }

        // 🚪 VÍA DE ESCAPE: Si alguien intenta loguearse o registrarse, no le pedimos token
        if (path.contains("/auth/")) {
            return chain.filter(exchange);
        }

        // 🚪 VÍA DE ESCAPE 2: Si es un GET al catálogo, permitimos que pase como usuario anónimo
        if (HttpMethod.GET.equals(method) && path.contains("/books")) {
            String authHeader = exchange.getRequest().getHeaders().entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                    .map(entry -> entry.getValue().get(0))
                    .findFirst()
                    .orElse(null);

            if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                log.info("Acceso público al catálogo: Dejando pasar petición GET como usuario anónimo.");
                return chain.filter(exchange);
            }
        }

        // 1. BUSCA EL TOKEN SIN IMPORTAR MAYÚSCULAS/MINÚSCULAS ("authorization" vs "Authorization")
        String authHeader = exchange.getRequest().getHeaders().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                .map(entry -> entry.getValue().get(0))
                .findFirst()
                .orElse(null);

        // 2. Validación tolerante al caso
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            log.warn("Petición rechazada: Falta la cabecera Authorization o no es Bearer");
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        // 3. Aislar el token opaco (el fantasma)
        String opaqueToken = authHeader.substring(7);

        // 4. Ir a buscarlo a Redis de forma reactiva
        return redisTemplate.opsForValue().get(opaqueToken)
                .flatMap(jwtReal -> {
                    log.info("¡Token Fantasma válido! Intercambiado por JWT real de forma exitosa.");

                    // 5. MUTA la petición reemplazando el token original por el JWT real con "A" estándar
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtReal)
                            .build();

                    // Sigue el camino con la petición modificada
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                // Si el token opaco no existe en Redis, se va por aquí:
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Token Fantasma inválido o expirado en Redis: {}", opaqueToken);
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }));
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
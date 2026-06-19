package com.unir.orders.service.controller;

import com.unir.orders.service.dto.CreateOrderRequest;
import com.unir.orders.service.dto.OrderResponse;
import com.unir.orders.service.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna automáticamente un estado 201 Created al completarse
    public OrderResponse create(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("accessToken") String jwt) { // 👈 La cabecera se declara AQUÍ, como parámetro del método

        // Pasamos tanto la petición como el JWT al servicio para procesar la lógica de negocio
        return ordersService.create(request, jwt);
    }

    @GetMapping("/users/{userId}/recent")
    public List<OrderResponse> getRecentOrdersByUser(@PathVariable String userId) {
        return ordersService.getRecentOrdersByUser(userId);
    }
}
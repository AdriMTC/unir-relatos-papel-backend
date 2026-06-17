package com.unir.orders.service.controller;

import com.unir.orders.service.dto.CreateOrderRequest;
import com.unir.orders.service.dto.OrderResponse;
import com.unir.orders.service.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


// interruptor que transforma una clase común de Java en una API web pública.
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return ordersService.create(request);
    }

    @GetMapping("/users/{userId}/recent")
    public List<OrderResponse> getRecentOrdersByUser(@PathVariable String userId) {
        return ordersService.getRecentOrdersByUser(userId);
    }
}


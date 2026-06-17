package com.unir.orders.service.service;

import com.unir.orders.service.client.CatalogueClient;
import com.unir.orders.service.dto.CatalogueBookResponse;
import com.unir.orders.service.dto.CreateOrderRequest;
import com.unir.orders.service.dto.OrderResponse;
import com.unir.orders.service.event.OrderCreatedEvent;
import com.unir.orders.service.exception.InvalidOrderException;
import com.unir.orders.service.exception.ResourceNotFoundException;
import com.unir.orders.service.model.Order;
import com.unir.orders.service.publisher.OrderEventPublisher;
import com.unir.orders.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrderRepository orderRepository;
    private final CatalogueClient catalogueClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderResponse create(CreateOrderRequest request) {
        CatalogueBookResponse book = catalogueClient.getBookById(request.getBookId());
        validateBook(book);

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setBookId(book.getId());
        order.setBookTitle(book.getTitle());
        order.setQuantity(request.getQuantity());
        order.setStatus("CREATED");

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .customerEmail(request.getCustomerEmail())
                .customerName(request.getCustomerName())
                .bookTitle(savedOrder.getBookTitle())
                .quantity(savedOrder.getQuantity())
                .createdAt(savedOrder.getCreatedAt())
                .build();
        orderEventPublisher.publishOrderCreated(event);

        return OrderResponse.fromEntity(savedOrder);
    }

    public List<OrderResponse> getRecentOrdersByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    private void validateBook(CatalogueBookResponse book) {
        //
        if (book == null || book.getId() == null) {
            throw new ResourceNotFoundException("Libro no encontrado en catalogue");
        }
        if (!Boolean.TRUE.equals(book.getVisible())) {
            throw new InvalidOrderException("El libro no esta visible en catalogue");
        }
    }
}


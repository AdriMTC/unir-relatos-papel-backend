package com.unir.orders.service.service;

import com.unir.orders.service.client.CatalogueClient;
import com.unir.orders.service.config.RabbitMQConfig;
import com.unir.orders.service.dto.CatalogueBookResponse;
import com.unir.orders.service.dto.CreateOrderRequest;
import com.unir.orders.service.dto.OrderCreatedEvent;
import com.unir.orders.service.dto.OrderResponse;
import com.unir.orders.service.exception.InvalidOrderException;
import com.unir.orders.service.exception.ResourceNotFoundException;
import com.unir.orders.service.model.Order;
import com.unir.orders.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrderRepository orderRepository;
    private final CatalogueClient catalogueClient;
    private final RabbitTemplate rabbitTemplate;

    public OrderResponse create(CreateOrderRequest request) {
        CatalogueBookResponse book = catalogueClient.getBookById(request.getBookId());
        validateBook(book);

        BigDecimal total = book.getPrice() != null
                ? book.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
                : BigDecimal.ZERO;

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setUserEmail(request.getUserEmail());
        order.setBookId(book.getId());
        order.setBookTitle(book.getTitle());
        order.setQuantity(request.getQuantity());
        order.setTotal(total);
        order.setStatus("CREATED");

        Order saved = orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .userEmail(saved.getUserEmail())
                .bookTitle(saved.getBookTitle())
                .quantity(saved.getQuantity())
                .total(saved.getTotal())
                .createdAt(saved.getCreatedAt())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ORDER_CREATED_KEY, event);
        log.info("Evento OrderCreated publicado para orderId={}", saved.getId());

        return OrderResponse.fromEntity(saved);
    }

    public List<OrderResponse> getRecentOrdersByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    private void validateBook(CatalogueBookResponse book) {
        if (book == null || book.getId() == null) {
            throw new ResourceNotFoundException("Libro no encontrado en catalogue");
        }
        if (!Boolean.TRUE.equals(book.getVisible())) {
            throw new InvalidOrderException("El libro no esta visible en catalogue");
        }
    }
}


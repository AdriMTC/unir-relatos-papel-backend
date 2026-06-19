package com.unir.orders.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable; // 👈 ¡Obligatorio para RabbitMQ!
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    // Es una buena práctica de Java añadir el serialVersionUID
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String userId;
    private String userEmail;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal total;
    private LocalDateTime createdAt;
}

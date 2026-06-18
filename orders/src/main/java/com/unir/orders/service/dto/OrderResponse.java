package com.unir.orders.service.dto;

import com.unir.orders.service.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {

    private Long id;
    private String userId;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setUserEmail(order.getUserEmail());
        response.setBookId(order.getBookId());
        response.setBookTitle(order.getBookTitle());
        response.setQuantity(order.getQuantity());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}


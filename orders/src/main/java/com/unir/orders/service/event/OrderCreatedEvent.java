package com.unir.orders.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {

    private Long orderId;
    private String userId;
    private String customerEmail;
    private String customerName;
    private String bookTitle;
    private Integer quantity;
    private LocalDateTime createdAt;
}

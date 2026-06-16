package com.unir.comms.service.listener;

import com.unir.comms.service.config.RabbitMQConfig;
import com.unir.comms.service.dto.OrderCreatedEvent;
import com.unir.comms.service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Evento recibido — order.created: pedido #{} usuario {}",
                event.getOrderId(), event.getUserId());

        if (event.getUserEmail() == null || event.getUserEmail().isBlank()) {
            log.warn("Pedido #{} sin email de usuario — se omite envío", event.getOrderId());
            return;
        }

        emailService.sendOrderConfirmation(
                event.getUserEmail(),
                event.getOrderId(),
                event.getBookTitle(),
                event.getQuantity(),
                event.getTotal()
        );
    }
}

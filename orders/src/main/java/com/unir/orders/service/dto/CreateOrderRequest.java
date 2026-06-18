package com.unir.orders.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "El userId es obligatorio")
    private String userId;

    @Email(message = "El userEmail debe ser un email válido")
    @NotBlank(message = "El userEmail es obligatorio")
    private String userEmail;

    @NotNull(message = "El bookId es obligatorio")
    private Long bookId;

    @NotNull(message = "La quantity es obligatoria")
    @Min(value = 1, message = "La quantity debe ser mayor o igual a 1")
    private Integer quantity;
}


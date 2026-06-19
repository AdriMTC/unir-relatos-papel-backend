package com.unir.catalogue.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class BookDTO {
    
    private String title;
    private String author;
    private LocalDate publicationDate;
    private String category;
    private String isbn;
    private Integer rating;
    private Boolean visible;
    private Integer stock;
    private BigDecimal price;
    private String image;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}

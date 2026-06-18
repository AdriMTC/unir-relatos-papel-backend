package com.unir.catalogue.service.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Document(indexName = "books", createIndex = false)
public class BookDocument {

    @Id
    private Long id;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private String category;
    private String isbn;
    private Integer rating;
    private Boolean visible;
    private Integer stock;
    private BigDecimal price;
}
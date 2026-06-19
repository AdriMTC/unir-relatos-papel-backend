package com.unir.catalogue.service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "book")
public class Book extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    private String category;

    @Column(unique = true, length = 30)
    private String isbn;

    private Integer rating;

    @ColumnDefault("true")
    private Boolean visible;

    private Integer stock;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;
}
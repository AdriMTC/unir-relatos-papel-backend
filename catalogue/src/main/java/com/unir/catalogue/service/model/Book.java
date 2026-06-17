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
@Table(name="book")
public class Book extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name="publication_date")
    private LocalDate publicationDate;

    @Column(name="category")
    private String category;

    @Column(name="isbn", unique=true , length=30)
    private String isbn;

    @Column(name="rating")
    private Integer rating;

    @ColumnDefault("true")
    @Column(name="visible")
    private Boolean visible;

    @Column(name="stock")
    private Integer stock;

    @Column(name="price" ,precision = 10, scale = 2)
    private BigDecimal price;

}

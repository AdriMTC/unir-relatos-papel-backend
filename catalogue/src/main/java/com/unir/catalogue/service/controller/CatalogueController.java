package com.unir.catalogue.service.controller;

import com.unir.catalogue.service.dto.BookDTO;
import com.unir.catalogue.service.model.Book;
import com.unir.catalogue.service.search.BookDocument;
import com.unir.catalogue.service.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    // CONSULTAS (ELASTICSEARCH)
    @GetMapping
    public List<BookDocument> getAll() {
        return catalogueService.getAll();
    }

    @GetMapping("/{id}")
    public BookDocument getById(@PathVariable Long id) {
        return catalogueService.getById(id);
    }

    @GetMapping("/search")
    public List<BookDocument> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate publicationDate
    ) {
        return catalogueService.search(
                title,
                author,
                category,
                isbn,
                rating,
                visible,
                publicationDate
        );
    }

    // ESCRITURAS (POSTGRESQL)
    @PostMapping
    public Book create(@RequestBody BookDTO dto) {
        return catalogueService.create(dto);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id,@RequestBody BookDTO dto) {
        return catalogueService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public Book partialUpdate(@PathVariable Long id,@RequestBody BookDTO dto) {
        return catalogueService.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        catalogueService.delete(id);
    }

    // REINDEXACIÓN
    @PostMapping("/reindex")
    public String reindex() {
        catalogueService.reindex();
        return "Indexación completada";
    }
}
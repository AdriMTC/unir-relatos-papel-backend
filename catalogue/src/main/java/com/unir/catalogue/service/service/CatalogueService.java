package com.unir.catalogue.service.service;

import com.unir.catalogue.service.dto.BookDTO;
import com.unir.catalogue.service.exception.ResourceNotFoundException;
import com.unir.catalogue.service.model.Book;
import com.unir.catalogue.service.repository.CatalogueRepository;
import com.unir.catalogue.service.search.BookDocument;
import com.unir.catalogue.service.search.BookMapper;
import com.unir.catalogue.service.search.BookSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CatalogueRepository catalogueRepository;
    private final BookSearchRepository searchRepository;
    private final BookMapper mapper;
    private final ElasticsearchOperations elasticsearchOperations;

    //CONSULTAS->ELASTICSEARCH

    public List<BookDocument> getAll() {
        return StreamSupport
                .stream(searchRepository.findAll().spliterator(), false)
                .toList();
    }

    public BookDocument getById(Long id) {
        return searchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Libro no encontrado"));
    }

    public List<BookDocument> search(
            String title,
            String author,
            String category,
            String isbn,
            Integer rating,
            Boolean visible,
            LocalDate publicationDate
    ) {

        List<Query> queries = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            queries.add(Query.of(q -> q.match(
                    m -> m.field("title").query(title))));
        }

        if (author != null && !author.isBlank()) {
            queries.add(Query.of(q -> q.match(
                    m -> m.field("author").query(author))));
        }

        if (category != null && !category.isBlank()) {
            queries.add(Query.of(q -> q.match(
                    m -> m.field("category").query(category))));
        }

        if (isbn != null && !isbn.isBlank()) {
            queries.add(Query.of(q -> q.match(
                    m -> m.field("isbn").query(isbn))));
        }

        if (rating != null) {
            queries.add(Query.of(q -> q.term(
                    t -> t.field("rating").value(rating))));
        }

        if (visible != null) {
            queries.add(Query.of(q -> q.term(
                    t -> t.field("visible").value(visible))));
        }

        NativeQuery query;

        if (queries.isEmpty()) {

            query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .build();

        } else {

            query = NativeQuery.builder()
                    .withQuery(q -> q.bool(
                            b -> b.must(queries)))
                    .build();
        }

        SearchHits<BookDocument> hits =
                elasticsearchOperations.search(
                        query,
                        BookDocument.class);

        return hits.stream()
                .map(hit -> hit.getContent())
                .toList();
    }

    // ESCRITURAS -> POSTGRESQL + Elasticsearch
    public Book create(BookDTO dto) {
        Book saved = catalogueRepository.save(mapToEntity(dto));
        searchRepository.save(mapper.toDocument(saved));
        return saved;
    }

    public Book update(Long id, BookDTO dto) {

        Book book = catalogueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Libro no encontrado"));
        updateFields(book, dto);
        Book updated = catalogueRepository.save(book);
        searchRepository.save(mapper.toDocument(updated));
        return updated;
    }

    public Book partialUpdate(Long id, BookDTO dto) {

        Book book = catalogueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Libro no encontrado"));
        updateFields(book, dto);
        Book updated = catalogueRepository.save(book);
        searchRepository.save(mapper.toDocument(updated));
        return updated;
    }

    public void delete(Long id) {
        catalogueRepository.deleteById(id);
        searchRepository.deleteById(id);
    }

    // REINDEX
    public void reindex() {
        List<Book> books = catalogueRepository.findAll();
        books.forEach(book ->
                searchRepository.save(mapper.toDocument(book)));
    }

    // MAPPERS
    private Book mapToEntity(BookDTO dto) {
        Book book = new Book();
        updateFields(book, dto);
        return book;
    }

    private void updateFields(Book book, BookDTO dto) {

        if (dto.getTitle() != null)
            book.setTitle(dto.getTitle());

        if (dto.getAuthor() != null)
            book.setAuthor(dto.getAuthor());

        if (dto.getCategory() != null)
            book.setCategory(dto.getCategory());

        if (dto.getIsbn() != null)
            book.setIsbn(dto.getIsbn());

        if (dto.getRating() != null)
            book.setRating(dto.getRating());

        if (dto.getVisible() != null)
            book.setVisible(dto.getVisible());

        if (dto.getPublicationDate() != null)
            book.setPublicationDate(dto.getPublicationDate());

        if (dto.getStock() != null)
            book.setStock(dto.getStock());

        if (dto.getPrice() != null)
            book.setPrice(dto.getPrice());
    }
}
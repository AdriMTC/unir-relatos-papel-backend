package com.unir.catalogue.service.service;

import com.unir.catalogue.service.dto.BookDTO;
import com.unir.catalogue.service.exception.ResourceNotFoundException;
import com.unir.catalogue.service.model.Book;
import com.unir.catalogue.service.repository.CatalogueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.unir.catalogue.service.specification.CatalogueSpecification;
import com.unir.catalogue.service.search.BookDocument;
import com.unir.catalogue.service.search.BookMapper;
import com.unir.catalogue.service.search.BookSearchRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CatalogueRepository catalogueRepository;
    private final CatalogueRepository catalogueRepository;

    private final BookSearchRepository searchRepository;

    private final BookMapper mapper;

    public List<Book> getAll() {
        return StreamSupport.stream(searchRepository.findAll()
            .spliterator(),false).toList();
    }

    public Book getById(Long id) {
        return searchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
    }

    public Book create(BookDTO dto) {
        Book saved = catalogueRepository.save(mapToEntity(dto));
        searchRepository.save(mapper.toDocument(saved));
        return saved;
    }

    public Book update(Long id, BookDTO dto) {
        Book book = getById(id);
        updateFields(book, dto);
        Book updated = catalogueRepository.save(book);
        searchRepository.save(mapper.toDocument(updated));
        return updated;
    }

    public Book partialUpdate(Long id, BookDTO dto) {
        Book book = getById(id);
        updateFields(book, dto);
        Book updated = catalogueRepository.save(book);
        searchRepository.save(mapper.toDocument(updated));
        return updated;
    }

    public void delete(Long id) {
        catalogueRepository.deleteById(id);
        searchRepository.deleteById(id);
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

        if(title != null)
            return searchRepository.findByTitleContaining(title);

        if(author != null)
            return searchRepository.findByAuthorContaining(author);

        if(category != null)
            return searchRepository.findByCategory(category);

        if(isbn != null)
            return searchRepository.findByIsbn(isbn);

        return StreamSupport.stream(searchRepository.findAll().spliterator(),false).toList();
    }

    private Book mapToEntity(BookDTO dto) {
        Book book = new Book();
        updateFields(book, dto);
        return book;
    }

    private void updateFields(Book book, BookDTO dto) {
        if (dto.getTitle() != null) book.setTitle(dto.getTitle());
        if (dto.getAuthor() != null) book.setAuthor(dto.getAuthor());
        if (dto.getCategory() != null) book.setCategory(dto.getCategory());
        if (dto.getIsbn() != null) book.setIsbn(dto.getIsbn());
        if (dto.getRating() != null) book.setRating(dto.getRating());
        if (dto.getVisible() != null) book.setVisible(dto.getVisible());
        if (dto.getPublicationDate() != null) book.setPublicationDate(dto.getPublicationDate());
        if (dto.getStock() != null) book.setStock(dto.getStock());
        if (dto.getPrice() != null) book.setPrice(dto.getPrice());
    }

    public void reindex() {
    List<Book> books = catalogueRepository.findAll();
    books.forEach(book -> searchRepository.save(mapper.toDocument(book)));
    }
}

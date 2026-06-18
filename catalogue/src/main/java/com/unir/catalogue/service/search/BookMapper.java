package com.unir.catalogue.service.search;

import com.unir.catalogue.service.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDocument toDocument(Book book) {

        System.out.println(
                "Libro: " + book.getTitle() + " fecha=" + book.getPublicationDate());

        BookDocument doc = new BookDocument();

        doc.setId(book.getId());
        doc.setTitle(book.getTitle());
        doc.setAuthor(book.getAuthor());
        doc.setPublicationDate(book.getPublicationDate());
        doc.setCategory(book.getCategory());
        doc.setIsbn(book.getIsbn());
        doc.setRating(book.getRating());
        doc.setVisible(book.getVisible());
        doc.setStock(book.getStock());
        doc.setPrice(book.getPrice());

        return doc;
    }
}
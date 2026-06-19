package com.unir.catalogue.service.search;

import com.unir.catalogue.service.model.Book;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class BookMapper {

    public BookDocument toDocument(Book book) {

        BookDocument doc = new BookDocument();

        doc.setId(book.getId());
        doc.setTitle(book.getTitle());
        doc.setAuthor(book.getAuthor());

        if (book.getPublicationDate() != null) {
            doc.setPublicationDate(
                    book.getPublicationDate()
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC)
            );
        }

        doc.setCategory(book.getCategory());
        doc.setIsbn(book.getIsbn());
        doc.setRating(book.getRating());
        doc.setVisible(book.getVisible());
        doc.setStock(book.getStock());
        doc.setPrice(book.getPrice());
        doc.setDescription(book.getDescription());
        doc.setImage(book.getImage());

        return doc;
    }
}
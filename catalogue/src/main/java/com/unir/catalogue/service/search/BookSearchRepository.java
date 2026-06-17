package com.unir.catalogue.service.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookSearchRepository
        extends ElasticsearchRepository<BookDocument, Long> {

    List<BookDocument> findByTitleContaining(String title);

    List<BookDocument> findByAuthorContaining(String author);

    List<BookDocument> findByCategory(String category);

    List<BookDocument> findByIsbn(String isbn);
}
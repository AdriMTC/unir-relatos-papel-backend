package com.unir.orders.service.client;

import com.unir.orders.service.dto.CatalogueBookResponse;
import com.unir.orders.service.exception.InvalidOrderException;
import com.unir.orders.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CatalogueClient {

    private final RestTemplate restTemplate;
    // no usa una ip
    private static final String CATALOGUE_BOOK_URL = "http://catalogue-service/api/v1/books/{id}";

    public CatalogueBookResponse getBookById(Long bookId) {
        try {
            return restTemplate.getForObject(CATALOGUE_BOOK_URL, CatalogueBookResponse.class, bookId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Libro no encontrado en catalogue");
        } catch (RestClientException ex) {
            throw new InvalidOrderException("No se pudo validar el libro en catalogue");
        }
    }
}


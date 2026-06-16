package com.unir.comms.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    private static final String SYSTEM_CONTEXT =
        "Eres un asistente de atención al cliente de la librería 'Relatos de Papel'. " +
        "Ayudas a los clientes con consultas sobre libros, pedidos y disponibilidad. " +
        "Responde siempre en el idioma del cliente. Sé amable y conciso. " +
        "Si no sabes algo concreto del inventario, invita al cliente a consultar el catálogo.";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getResponse(String userMessage) {
        try {
            String prompt = SYSTEM_CONTEXT + "\nCliente: " + userMessage;

            Map<String, Object> body = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            );

            String url = apiUrl + "?key=" + apiKey;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);

            return extractText(response);

        } catch (Exception e) {
            log.error("Error llamando a Gemini API: {}", e.getMessage());
            return "Lo siento, el asistente no está disponible en este momento. " +
                   "Por favor, intenta de nuevo más tarde.";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) return "Sin respuesta de la IA.";
        try {
            List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Error parseando respuesta de Gemini: {}", e.getMessage());
            return "No pude procesar la respuesta del asistente.";
        }
    }
}

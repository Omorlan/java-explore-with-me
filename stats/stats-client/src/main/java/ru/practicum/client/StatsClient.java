package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    private final RestTemplate rest;

    /**
     * Initializes StatsClient with a base URL for the stats server and a configured RestTemplate.
     *
     * @param builder RestTemplateBuilder for constructing a RestTemplate with custom settings.
     */
    public StatsClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://stats-server:9090"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    /**
     * Retrieves statistics for specified URIs within a given date range.
     *
     * @param start  Start date-time of the period (inclusive).
     * @param end    End date-time of the period (inclusive).
     * @param uris   List of URIs to filter statistics.
     * @param unique Whether to count only unique IP addresses.
     * @return ResponseEntity containing statistics data.
     */
    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, boolean unique) {
        String urlTemplate = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("uris", "{uris}")
                .queryParam("unique", "{unique}")
                .encode()
                .toUriString();
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return makeAndSendRequest(HttpMethod.GET, urlTemplate, parameters, null);
    }

    /**
     * Creates a new hit record on the stats server.
     *
     * @param body The request body containing hit information.
     * @param <T>  The type of the request body.
     * @return ResponseEntity containing the server's response.
     */
    public <T> ResponseEntity<Object> create(T body) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    /**
     * Sends an HTTP request to the stats server.
     *
     * @param method     HTTP method (GET, POST, etc.).
     * @param path       URL path for the request.
     * @param parameters Request parameters for URL templating.
     * @param body       Request body (optional).
     * @param <T>        Type of the request body.
     * @return ResponseEntity containing the server's response.
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = null;
        if (body != null) {
            requestEntity = new HttpEntity<>(body);
        }

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    /**
     * Prepares a standardized response for the client based on the server's response.
     *
     * @param response The original server response.
     * @return A ResponseEntity containing either the server's body or an empty response if none is present.
     */
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}

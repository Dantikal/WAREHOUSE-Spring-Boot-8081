package kg.bdc.warehouse.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReturnsService {

    @Value("${drivers-service.returns-url:http://localhost:8082/api/drivers/returns}")
    private String returnsUrl;

    @Value("${internal.api-key:}")
    private String internalApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<Object> forwardReturn(Long warehouseId, Map<String, Object> body, HttpServletRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (body != null) {
            payload.putAll(body);
        }
        payload.putIfAbsent("warehouseId", warehouseId);
        payload.putIfAbsent("warehouse_id", warehouseId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (internalApiKey != null && !internalApiKey.isBlank()) {
            headers.set("X-Api-Key", internalApiKey);
        }

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    returnsUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "drivers-service недоступен: " + e.getMessage()));
        }
    }
}

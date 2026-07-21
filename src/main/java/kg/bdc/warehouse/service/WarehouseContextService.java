package kg.bdc.warehouse.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kg.bdc.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseContextService {

    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;
    private final WarehouseRepository warehouseRepository;

    public Long requireWarehouseId(HttpServletRequest request) {
        UUID externalUuid = resolveWarehouseId(request)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Не удалось определить склад. Передайте X-Warehouse-Id или JWT claim warehouse_id/warehouseId"));
        return warehouseRepository.findByExternalUuid(externalUuid)
                .map(warehouse -> warehouse.getId())
                .orElseThrow(() -> new NoSuchElementException("Склад не найден: " + externalUuid));
    }

    public Optional<UUID> resolveWarehouseId(HttpServletRequest request) {
        String headerWarehouseId = request.getHeader("X-Warehouse-Id");
        if (headerWarehouseId != null) {
            return Optional.of(parseWarehouseUuid(headerWarehouseId)
                    .orElseThrow(() -> new IllegalArgumentException("Некорректный X-Warehouse-Id")));
        }
        return resolveFromAuthorization(request.getHeader("Authorization"));
    }

    private Optional<UUID> resolveFromAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authorization.substring("Bearer ".length()).trim();
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return Optional.empty();
        }

        try {
            byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
            String jsonPayload = new String(decodedPayload, StandardCharsets.UTF_8);
            Map<String, Object> claims = objectMapper.readValue(jsonPayload, CLAIMS_TYPE);
            return parseWarehouseUuid(claims.get("warehouse_id"))
                    .or(() -> parseWarehouseUuid(claims.get("warehouseId")));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private Optional<UUID> parseWarehouseUuid(Object value) {
        if (value instanceof String string) {
            return parseWarehouseUuid(string);
        }
        return Optional.empty();
    }

    private Optional<UUID> parseWarehouseUuid(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}

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

@Service
@RequiredArgsConstructor
public class WarehouseContextService {

    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;
    private final WarehouseRepository warehouseRepository;

    public Long requireWarehouseId(HttpServletRequest request) {
        Long warehouseId = resolveWarehouseId(request)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Не удалось определить склад. Передайте X-Warehouse-Id или JWT claim warehouse_id/warehouseId"));
        return validateWarehouseId(warehouseId);
    }

    public Optional<Long> resolveWarehouseId(HttpServletRequest request) {
        String headerWarehouseId = request.getHeader("X-Warehouse-Id");
        if (headerWarehouseId != null) {
            return Optional.of(parseWarehouseId(headerWarehouseId)
                    .orElseThrow(() -> new IllegalArgumentException("Некорректный X-Warehouse-Id")));
        }
        return resolveFromAuthorization(request.getHeader("Authorization"));
    }

    public Long validateWarehouseId(Long warehouseId) {
        if (warehouseId == null || warehouseId <= 0) {
            throw new IllegalArgumentException("Некорректный ID склада");
        }
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new NoSuchElementException("Склад не найден: " + warehouseId);
        }
        return warehouseId;
    }

    private Optional<Long> resolveFromAuthorization(String authorization) {
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
            return parseWarehouseId(claims.get("warehouse_id"))
                    .or(() -> parseWarehouseId(claims.get("warehouseId")));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private Optional<Long> parseWarehouseId(Object value) {
        if (value instanceof Number number) {
            return Optional.of(number.longValue()).filter(id -> id > 0);
        }
        if (value instanceof String string) {
            return parseWarehouseId(string);
        }
        return Optional.empty();
    }

    private Optional<Long> parseWarehouseId(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(value.trim())).filter(id -> id > 0);
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}

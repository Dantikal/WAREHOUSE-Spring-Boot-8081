package kg.bdc.warehouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kg.bdc.warehouse.entity.Warehouse;
import kg.bdc.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WarehouseContextServiceTest {

    private static final UUID HEADER_UUID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    private static final UUID JWT_UUID = UUID.fromString("e0ecf501-88d4-4f7c-abdb-ed6d3b5996bd");

    @Test
    void headerUuidTakesPriorityOverJwt() {
        List<UUID> lookups = new ArrayList<>();
        WarehouseContextService service = service(Map.of(HEADER_UUID, warehouse(2L, HEADER_UUID)), lookups);
        HttpServletRequest request = request(Map.of(
                "X-Warehouse-Id", HEADER_UUID.toString(),
                "Authorization", bearerToken(JWT_UUID)));

        assertThat(service.requireWarehouseId(request)).isEqualTo(2L);
        assertThat(lookups).containsExactly(HEADER_UUID);
    }

    @Test
    void jwtUuidIsConvertedToInternalIdWhenHeaderIsAbsent() {
        WarehouseContextService service = service(Map.of(JWT_UUID, warehouse(3L, JWT_UUID)), new ArrayList<>());

        assertThat(service.requireWarehouseId(request(Map.of("Authorization", bearerToken(JWT_UUID)))))
                .isEqualTo(3L);
    }

    @Test
    void invalidHeaderDoesNotFallBackToJwt() {
        List<UUID> lookups = new ArrayList<>();
        WarehouseContextService service = service(Map.of(JWT_UUID, warehouse(1L, JWT_UUID)), lookups);
        HttpServletRequest request = request(Map.of(
                "X-Warehouse-Id", "1",
                "Authorization", bearerToken(JWT_UUID)));

        assertThatThrownBy(() -> service.requireWarehouseId(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Некорректный X-Warehouse-Id");
        assertThat(lookups).isEmpty();
    }

    @Test
    void missingHeaderAndJwtIsRejected() {
        WarehouseContextService service = service(Map.of(), new ArrayList<>());

        assertThatThrownBy(() -> service.requireWarehouseId(request(Map.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Не удалось определить склад");
    }

    @Test
    void unknownExternalUuidIsRejected() {
        WarehouseContextService service = service(Map.of(), new ArrayList<>());

        assertThatThrownBy(() -> service.requireWarehouseId(
                request(Map.of("X-Warehouse-Id", HEADER_UUID.toString()))))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Склад не найден: " + HEADER_UUID);
    }

    private WarehouseContextService service(Map<UUID, Warehouse> warehouses, List<UUID> lookups) {
        WarehouseRepository repository = (WarehouseRepository) Proxy.newProxyInstance(
                WarehouseRepository.class.getClassLoader(),
                new Class<?>[]{WarehouseRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findByExternalUuid")) {
                        UUID uuid = (UUID) args[0];
                        lookups.add(uuid);
                        return Optional.ofNullable(warehouses.get(uuid));
                    }
                    return defaultValue(method.getReturnType());
                });
        return new WarehouseContextService(new ObjectMapper(), repository);
    }

    private HttpServletRequest request(Map<String, String> headers) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getHeader")) {
                        return headers.get((String) args[0]);
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        return 0D;
    }

    private Warehouse warehouse(long internalId, UUID externalUuid) {
        return Warehouse.builder().id(internalId).externalUuid(externalUuid).build();
    }

    private String bearerToken(UUID warehouseId) {
        String payload = "{\"warehouse_id\":\"" + warehouseId + "\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return "Bearer header." + encodedPayload + ".signature";
    }
}

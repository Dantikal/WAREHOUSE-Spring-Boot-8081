package kg.bdc.warehouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kg.bdc.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WarehouseContextServiceTest {

    private HttpServletRequest request;
    private WarehouseRepository warehouseRepository;
    private WarehouseContextService service;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        warehouseRepository = mock(WarehouseRepository.class);
        service = new WarehouseContextService(new ObjectMapper(), warehouseRepository);
    }

    @Test
    void headerTakesPriorityOverJwt() {
        when(request.getHeader("X-Warehouse-Id")).thenReturn("2");
        when(request.getHeader("Authorization")).thenReturn(bearerToken(1));
        when(warehouseRepository.existsById(2L)).thenReturn(true);

        assertThat(service.requireWarehouseId(request)).isEqualTo(2L);
        verify(warehouseRepository).existsById(2L);
        verify(warehouseRepository, never()).existsById(1L);
    }

    @Test
    void jwtIsUsedWhenHeaderIsAbsent() {
        when(request.getHeader("Authorization")).thenReturn(bearerToken(3));
        when(warehouseRepository.existsById(3L)).thenReturn(true);

        assertThat(service.requireWarehouseId(request)).isEqualTo(3L);
    }

    @Test
    void invalidHeaderDoesNotFallBackToJwt() {
        when(request.getHeader("X-Warehouse-Id")).thenReturn("invalid");
        when(request.getHeader("Authorization")).thenReturn(bearerToken(1));

        assertThatThrownBy(() -> service.requireWarehouseId(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Некорректный X-Warehouse-Id");
        verify(warehouseRepository, never()).existsById(1L);
    }

    @Test
    void missingHeaderAndJwtIsRejected() {
        when(request.getParameter("warehouse_id")).thenReturn("1");

        assertThatThrownBy(() -> service.requireWarehouseId(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Не удалось определить склад");
    }

    private String bearerToken(long warehouseId) {
        String payload = "{\"warehouse_id\":" + warehouseId + "}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return "Bearer header." + encodedPayload + ".signature";
    }
}

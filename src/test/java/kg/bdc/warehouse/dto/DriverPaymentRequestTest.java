package kg.bdc.warehouse.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DriverPaymentRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void acceptsUuidDriverId() throws Exception {
        DriverPaymentRequest request = objectMapper.readValue("""
                {
                  "driverId": "550e8400-e29b-41d4-a716-446655440000",
                  "amount": 15000.00,
                  "paymentMethod": "CASH"
                }
                """, DriverPaymentRequest.class);

        assertThat(request.getDriverId())
                .isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    }
}

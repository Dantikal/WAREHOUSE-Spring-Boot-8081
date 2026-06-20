package kg.bdc.warehouse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI warehouseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bishkek Distribution Cold Warehouse API")
                        .description("""
                                ## Система управления складом Бишкекского дистрибьюционного холодильника
                                
                                ### Возможности API:
                                - **Склады** — управление складами и статистика
                                - **Инвентаризация** — остатки, движение товаров, низкий остаток
                                - **Выдачи** — создание и управление выдачами водителям
                                - **Касса** — баланс и операции кассы
                                - **WebSocket** — real-time уведомления через STOMP
                                
                                ### WebSocket подключение:
                                ```
                                ws://localhost:8080/ws
                                Subscribe: /topic/dashboard/update
                                Subscribe: /topic/inventory/low-stock
                                Subscribe: /topic/dispatch/created
                                Subscribe: /topic/orders/new
                                ```
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BDC Development Team")
                                .email("dev@bdc.kg")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")))
                .tags(List.of(
                        new Tag().name("Warehouses").description("Управление складами"),
                        new Tag().name("Inventory").description("Остатки и движение товаров"),
                        new Tag().name("Dispatch").description("Выдача товаров водителям"),
                        new Tag().name("Cashbox").description("Касса и финансовые операции"),
                        new Tag().name("WebSocket").description("Real-time уведомления (STOMP)")
                ));
    }
}

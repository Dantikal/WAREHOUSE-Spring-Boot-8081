# 🏭 Bishkek Distribution Cold Warehouse — Backend API

Spring Boot REST API + WebSocket для системы управления складом Бишкекского дистрибьюционного холодильника.

---

## Технологический стек

| Слой | Технология |
|------|-----------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Persistence | Spring Data JPA (Hibernate) |
| Database | PostgreSQL (prod) / H2 (dev) |
| Migrations | Flyway |
| Real-time | WebSocket STOMP |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven 3.9 |

---

## Быстрый старт

### Вариант 1 — H2 (без PostgreSQL, запускается сразу)

```bash
./mvnw spring-boot:run
```

Приложение поднимется на `http://localhost:8080`.  
База данных заполнена тестовыми данными автоматически через Flyway:
- 2 склада
- 8 продуктов (мороженое)
- 5 водителей
- Остатки, выдачи, касса, долги

### Вариант 2 — PostgreSQL

```bash
# 1. Создать БД
psql -U postgres -f init-postgres.sql

# 2. Запустить с профилем postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Вариант 3 — Docker Compose (PostgreSQL + App)

```bash
docker compose up -d
```

---

## Документация

| URL | Описание |
|-----|----------|
| `http://localhost:8080/swagger-ui.html` | **Swagger UI** — интерактивный тест всех endpoints |
| `http://localhost:8080/api-docs` | OpenAPI JSON spec |
| `http://localhost:8080/h2-console` | H2 Console (только dev) |

---

## REST API — все endpoints

Base: `/api/warehouse`

Для endpoints "свой склад" сервис берет склад из `X-Warehouse-Id` или JWT claim `warehouse_id` / `warehouseId`.
Для обратной совместимости также поддерживается query `warehouse_id`.

### Склады
```
GET /warehouses              — список складов
GET /warehouses/{id}         — склад по ID
GET /warehouses/{id}/stats   — статистика склада
GET /stats/country           — статистика по стране
```

### Дашборд
```
GET /dashboard               — данные главного экрана по своему складу
```

### Остатки
```
GET /inventory               — остатки своего склада
GET /inventory/low-stock     — низкий остаток (≤5 кор.) своего склада
GET /inventory/movements     — история движений своего склада
```

### Выдача
```
POST /dispatch               — создать выдачу
GET  /dispatch?driver_id=    — список выдач своего склада, опционально по водителю
GET  /dispatch/{id}          — выдача по ID в своем складе
PUT  /dispatch/{id}/status   — изменить статус выдачи
POST /dispatch/{id}/confirm  — подтверждение из drivers-service
```

### Касса
```
GET  /cashbox/balance          — баланс кассы своего склада
GET  /cashbox/transactions     — операции кассы своего склада
POST /cashbox/driver-payment   — платеж водителя
POST /cashbox/factory-payment  — платеж заводу
```

### Возвраты
```
POST /returns                — форвард возврата в drivers-service
```

### WebSocket тест-триггеры (Swagger)
```
POST /api/warehouse/websocket/trigger/dashboard-update
POST /api/warehouse/websocket/trigger/low-stock
POST /api/warehouse/websocket/trigger/dispatch-created
POST /api/warehouse/websocket/trigger/new-order
```

---

## WebSocket

**Endpoint подключения:**
```
ws://localhost:8080/ws            (SockJS)
ws://localhost:8080/ws/websocket  (Native STOMP)
```

**Топики:**

| Топик | Событие | Payload |
|-------|---------|---------|
| `/topic/dashboard/update` | Изменился баланс / долги | `{"type":"DASHBOARD_UPDATED"}` |
| `/topic/inventory/low-stock` | Остаток товара ≤ 5 кор. | `{"productId":1,"productName":"..."}` |
| `/topic/dispatch/created` | Создана новая выдача | `{"dispatchId":10}` |
| `/topic/orders/new` | Новая заявка водителя | `{"orderId":5}` |

**Пример подключения (JavaScript):**
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);

client.connect({}, () => {
  client.subscribe('/topic/dashboard/update', msg => {
    console.log('Dashboard updated:', JSON.parse(msg.body));
  });
  client.subscribe('/topic/inventory/low-stock', msg => {
    const data = JSON.parse(msg.body);
    alert(`Низкий остаток: ${data.productName}`);
  });
});
```

---

## Бизнес-логика

### POST /api/warehouse/dispatch — создание выдачи
1. Проверяет наличие склада, водителя и всех товаров
2. Проверяет достаточность остатка на складе
3. Уменьшает `inventory.quantity_boxes` для каждой позиции
4. Увеличивает `driver.debt` на сумму выдачи
5. Генерирует номер накладной `INV-YYYYMMDD-HHMMSS`
6. Создаёт записи `inventory_movement` для каждой позиции
7. При остатке ≤ 5 кор. → WS `/topic/inventory/low-stock`
8. WS → `/topic/dispatch/created`
9. WS → `/topic/dashboard/update`

---

## Структура проекта

```
src/main/java/kg/bdc/warehouse/
├── WarehouseApplication.java
├── config/
│   ├── GlobalExceptionHandler.java
│   ├── OpenApiConfig.java
│   └── WebSocketConfig.java
├── controller/
│   ├── WarehouseController.java
│   ├── InventoryController.java
│   ├── DispatchController.java
│   ├── CashboxController.java
│   └── WebSocketController.java      ← WS тест-триггеры
├── dto/                               ← 17 DTO классов
├── entity/                            ← 11 JPA Entity
├── repository/                        ← 9 репозиториев
├── service/                           ← 4 сервиса
└── websocket/
    └── WebSocketNotificationService.java

src/main/resources/
├── application.yml                    ← H2 (dev, default)
├── application-postgres.yml           ← PostgreSQL (prod)
└── db/migration/
    ├── V1__init.sql                   ← схема + seed данные
    └── V2__indexes.sql                ← индексы
```

---

## Тестовые данные (seed)

После запуска доступны:

**Склады:** Bishkek Cold Warehouse #1 (id=1), #2 (id=2)

**Продукты (id 1-8):**
- Пломбир Ванильный, Эскимо, Мороженое Ягодное, Торт-мороженое
- Фруктовый лёд, Пломбир Клубничный, Рожок Сливочный ⚠️, Крем-брюле ⚠️

**Водители (id 1-5):** Иванов, Джумабеков, Токтобеков, Абдрахманов, Мамытбеков

**Тест выдачи через Swagger:**
```json
POST /api/warehouse/dispatch
{
  "warehouseId": 1,
  "driverId": 4,
  "items": [
    {"productId": 1, "quantityBoxes": 5},
    {"productId": 2, "quantityBoxes": 10}
  ]
}
```

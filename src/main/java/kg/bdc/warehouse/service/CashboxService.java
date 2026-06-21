package kg.bdc.warehouse.service;

import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.entity.*;
import kg.bdc.warehouse.repository.*;
import kg.bdc.warehouse.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CashboxService {

    private final CashboxRepository cashboxRepository;
    private final CashboxTransactionRepository transactionRepository;
    private final WarehouseRepository warehouseRepository;
    private final DriverRepository driverRepository;
    private final DebtRepository debtRepository;
    private final DriverPaymentRepository driverPaymentRepository;
    private final FactoryPaymentRepository factoryPaymentRepository;
    private final WebSocketNotificationService wsService;

    public CashboxBalanceDto getBalance(Long warehouseId) {
        BigDecimal balance = cashboxRepository.findByWarehouseId(warehouseId)
                .map(c -> c.getCurrentBalance())
                .orElseThrow(() -> new NoSuchElementException("Касса склада не найдена: " + warehouseId));
        return new CashboxBalanceDto(balance);
    }

    public List<CashboxTransactionDto> getTransactions(Long warehouseId) {
        return transactionRepository.findByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CashboxTransactionDto createDriverPayment(Long warehouseId, DriverPaymentRequest req) {
        validateAmount(req.getAmount());

        Warehouse warehouse = getWarehouse(warehouseId);
        Driver driver = driverRepository.findById(req.getDriverId())
                .orElseThrow(() -> new NoSuchElementException("Водитель не найден: " + req.getDriverId()));
        Cashbox cashbox = getCashbox(warehouseId);

        cashbox.setCurrentBalance(zeroIfNull(cashbox.getCurrentBalance()).add(req.getAmount()));
        cashboxRepository.save(cashbox);

        driver.setDebt(subtractNotBelowZero(driver.getDebt(), req.getAmount()));
        driverRepository.save(driver);

        debtRepository.findByWarehouseIdAndDriverIdAndDebtType(warehouseId, driver.getId(), "DRIVER_DEBT")
                .ifPresent(debt -> {
                    debt.setAmount(subtractNotBelowZero(debt.getAmount(), req.getAmount()));
                    debtRepository.save(debt);
                });

        driverPaymentRepository.save(DriverPayment.builder()
                .warehouse(warehouse)
                .driver(driver)
                .amount(req.getAmount())
                .paymentMethod(normalizePaymentMethod(req.getPaymentMethod()))
                .comment(req.getComment())
                .build());

        CashboxTransaction saved = transactionRepository.save(CashboxTransaction.builder()
                .warehouse(warehouse)
                .transactionType("DRIVER_PAYMENT")
                .amount(req.getAmount())
                .paymentMethod(normalizePaymentMethod(req.getPaymentMethod()))
                .comment(req.getComment())
                .build());

        wsService.notifyDashboardUpdated();
        return toDto(saved);
    }

    @Transactional
    public CashboxTransactionDto createFactoryPayment(Long warehouseId, FactoryPaymentRequest req) {
        validateAmount(req.getAmount());

        Warehouse warehouse = getWarehouse(warehouseId);
        Cashbox cashbox = getCashbox(warehouseId);
        BigDecimal currentBalance = zeroIfNull(cashbox.getCurrentBalance());
        if (currentBalance.compareTo(req.getAmount()) < 0) {
            throw new IllegalStateException("Недостаточно средств в кассе склада");
        }

        cashbox.setCurrentBalance(currentBalance.subtract(req.getAmount()));
        cashboxRepository.save(cashbox);

        debtRepository.findByWarehouseIdAndDriverIsNullAndDebtType(warehouseId, "FACTORY_DEBT")
                .ifPresent(debt -> {
                    debt.setAmount(subtractNotBelowZero(debt.getAmount(), req.getAmount()));
                    debtRepository.save(debt);
                });

        factoryPaymentRepository.save(FactoryPayment.builder()
                .warehouse(warehouse)
                .amount(req.getAmount())
                .comment(req.getComment())
                .build());

        CashboxTransaction saved = transactionRepository.save(CashboxTransaction.builder()
                .warehouse(warehouse)
                .transactionType("FACTORY_PAYMENT")
                .amount(req.getAmount())
                .paymentMethod(normalizePaymentMethod(req.getPaymentMethod()))
                .comment(req.getComment())
                .build());

        wsService.notifyDashboardUpdated();
        return toDto(saved);
    }

    private Warehouse getWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NoSuchElementException("Склад не найден: " + warehouseId));
    }

    private Cashbox getCashbox(Long warehouseId) {
        return cashboxRepository.findByWarehouseId(warehouseId)
                .orElseThrow(() -> new NoSuchElementException("Касса склада не найдена: " + warehouseId));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма платежа должна быть больше 0");
        }
    }

    private BigDecimal subtractNotBelowZero(BigDecimal current, BigDecimal amount) {
        BigDecimal result = zeroIfNull(current).subtract(amount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizePaymentMethod(String paymentMethod) {
        return paymentMethod == null || paymentMethod.isBlank() ? "CASH" : paymentMethod.trim().toUpperCase();
    }

    private CashboxTransactionDto toDto(CashboxTransaction t) {
        return CashboxTransactionDto.builder()
                .id(t.getId())
                .type(t.getTransactionType())
                .amount(t.getAmount())
                .paymentMethod(t.getPaymentMethod())
                .comment(t.getComment())
                .createdAt(t.getCreatedAt())
                .build();
    }
}

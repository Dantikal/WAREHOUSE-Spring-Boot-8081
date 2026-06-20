package kg.bdc.warehouse.service;

import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.repository.CashboxRepository;
import kg.bdc.warehouse.repository.CashboxTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CashboxService {

    private final CashboxRepository cashboxRepository;
    private final CashboxTransactionRepository transactionRepository;

    public CashboxBalanceDto getBalance(Long warehouseId) {
        BigDecimal balance = cashboxRepository.findByWarehouseId(warehouseId)
                .map(c -> c.getCurrentBalance())
                .orElseThrow(() -> new NoSuchElementException("Касса склада не найдена: " + warehouseId));
        return new CashboxBalanceDto(balance);
    }

    public List<CashboxTransactionDto> getTransactions(Long warehouseId) {
        return transactionRepository.findByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                .stream()
                .map(t -> CashboxTransactionDto.builder()
                        .id(t.getId())
                        .type(t.getTransactionType())
                        .amount(t.getAmount())
                        .paymentMethod(t.getPaymentMethod())
                        .comment(t.getComment())
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();
    }
}

package kg.bdc.warehouse.service;

import kg.bdc.warehouse.dto.*;
import kg.bdc.warehouse.entity.Warehouse;
import kg.bdc.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final DriverRepository driverRepository;
    private final CashboxRepository cashboxRepository;
    private final DebtRepository debtRepository;
    private final DispatchRepository dispatchRepository;

    public List<WarehouseDto> getAll() {
        return warehouseRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public WarehouseDto getById(Long id) {
        return warehouseRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Склад не найден: " + id));
    }

    public WarehouseStatsDto getStats(Long warehouseId) {
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NoSuchElementException("Склад не найден: " + warehouseId));

        Long totalProducts = inventoryRepository.countProductsByWarehouse(warehouseId);
        Long totalDrivers = driverRepository.countByStatus("ACTIVE");
        BigDecimal inventoryValue = inventoryRepository.sumInventoryValue(warehouseId);
        BigDecimal cashBalance = cashboxRepository.findByWarehouseId(warehouseId)
                .map(c -> c.getCurrentBalance())
                .orElse(BigDecimal.ZERO);
        BigDecimal factoryDebt = debtRepository.sumFactoryDebtByWarehouse(warehouseId);

        return WarehouseStatsDto.builder()
                .totalProducts(totalProducts)
                .totalDrivers(totalDrivers)
                .inventoryValue(inventoryValue != null ? inventoryValue : BigDecimal.ZERO)
                .cashBalance(cashBalance)
                .factoryDebt(factoryDebt)
                .build();
    }

    public CountryStatsDto getCountryStats() {
        long warehouseCount = warehouseRepository.count();
        BigDecimal totalFactoryDebt = debtRepository.sumAllFactoryDebt();
        BigDecimal totalDriverDebt = debtRepository.sumAllDriverDebt();
        BigDecimal totalCash = cashboxRepository.findAll().stream()
                .map(c -> c.getCurrentBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CountryStatsDto.builder()
                .totalWarehouses(warehouseCount)
                .totalFactoryDebt(totalFactoryDebt)
                .totalDriversDebt(totalDriverDebt)
                .totalCashBalance(totalCash)
                .build();
    }

    public DashboardDto getDashboard() {
        // Aggregate first warehouse (or overall)
        BigDecimal cashBalance = cashboxRepository.findAll().stream()
                .map(c -> c.getCurrentBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal factoryDebt = debtRepository.sumAllFactoryDebt();
        BigDecimal driversDebt = driverRepository.sumAllDebts();

        Long lowStock = warehouseRepository.findAll().stream()
                .mapToLong(w -> inventoryRepository.countLowStock(w.getId()))
                .sum();

        long newOrders = dispatchRepository.findAll().stream()
                .filter(d -> "CREATED".equals(d.getStatus()))
                .count();

        return DashboardDto.builder()
                .cashBalance(cashBalance)
                .factoryDebt(factoryDebt)
                .totalDriversDebt(driversDebt)
                .lowStockProducts(lowStock)
                .newOrders(newOrders)
                .build();
    }

    private WarehouseDto toDto(Warehouse w) {
        return WarehouseDto.builder()
                .id(w.getId())
                .name(w.getName())
                .address(w.getAddress())
                .build();
    }
}

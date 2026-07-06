package com.company.assetmanagement.service;

import com.company.assetmanagement.dto.DashboardStatsDto;
import com.company.assetmanagement.dto.MaintenanceRecordDto;
import com.company.assetmanagement.entity.Asset;
import com.company.assetmanagement.entity.AssetStatus;
import com.company.assetmanagement.entity.MaintenanceStatus;
import com.company.assetmanagement.repository.AssetRepository;
import com.company.assetmanagement.repository.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AssetRepository assetRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;

    public DashboardStatsDto getStats() {
        List<Asset> allAssets = assetRepository.findAll();

        BigDecimal totalValue = allAssets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> byCategory = allAssets.stream()
                .filter(a -> a.getCategory() != null)
                .collect(Collectors.groupingBy(a -> a.getCategory().getName(), Collectors.counting()));

        List<MaintenanceRecordDto> upcoming = maintenanceRecordRepository
                .findByStatusAndScheduledDateBefore(MaintenanceStatus.SCHEDULED, LocalDate.now().plusDays(30))
                .stream()
                .map(m -> MaintenanceRecordDto.builder()
                        .id(m.getId())
                        .assetId(m.getAsset().getId())
                        .assetName(m.getAsset().getName())
                        .assetTag(m.getAsset().getAssetTag())
                        .type(m.getType())
                        .status(m.getStatus())
                        .scheduledDate(m.getScheduledDate())
                        .build())
                .sorted((a, b) -> a.getScheduledDate().compareTo(b.getScheduledDate()))
                .limit(10)
                .toList();

        return DashboardStatsDto.builder()
                .totalAssets(allAssets.size())
                .availableAssets(assetRepository.countByStatus(AssetStatus.AVAILABLE))
                .assignedAssets(assetRepository.countByStatus(AssetStatus.ASSIGNED))
                .inMaintenanceAssets(assetRepository.countByStatus(AssetStatus.IN_MAINTENANCE))
                .retiredAssets(assetRepository.countByStatus(AssetStatus.RETIRED))
                .totalAssetValue(totalValue)
                .upcomingMaintenanceCount(upcoming.size())
                .assetsByCategory(byCategory)
                .upcomingMaintenance(upcoming)
                .build();
    }
}

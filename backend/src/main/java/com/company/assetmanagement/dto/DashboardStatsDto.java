package com.company.assetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalAssets;
    private long availableAssets;
    private long assignedAssets;
    private long inMaintenanceAssets;
    private long retiredAssets;
    private BigDecimal totalAssetValue;
    private long upcomingMaintenanceCount;
    private Map<String, Long> assetsByCategory;
    private List<MaintenanceRecordDto> upcomingMaintenance;
}

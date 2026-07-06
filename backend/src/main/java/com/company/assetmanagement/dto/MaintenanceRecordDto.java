package com.company.assetmanagement.dto;

import com.company.assetmanagement.entity.MaintenanceStatus;
import com.company.assetmanagement.entity.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecordDto {
    private Long id;

    @NotNull(message = "Asset is required")
    private Long assetId;

    private String assetName;
    private String assetTag;

    @NotNull(message = "Maintenance type is required")
    private MaintenanceType type;

    private MaintenanceStatus status;

    private String description;

    private LocalDate scheduledDate;

    private LocalDate completedDate;

    private BigDecimal cost;

    private String performedBy;

    private String vendor;

    private String notes;
}

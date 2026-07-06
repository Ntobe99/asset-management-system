package com.company.assetmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetAssignmentRequest {
    @NotNull(message = "Employee is required")
    private Long employeeId;

    private String notes;
}

package com.company.assetmanagement.dto;

import com.company.assetmanagement.entity.AssetStatus;
import jakarta.validation.constraints.NotBlank;
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
public class AssetDto {
    private Long id;

    @NotBlank(message = "Asset tag is required")
    private String assetTag;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    private AssetStatus status;

    private String location;

    private String manufacturer;

    private String modelNumber;

    private String serialNumber;

    private LocalDate purchaseDate;

    private BigDecimal purchaseCost;

    private BigDecimal currentValue;

    private LocalDate warrantyExpiry;

    private Long assignedEmployeeId;

    private String assignedEmployeeName;
}

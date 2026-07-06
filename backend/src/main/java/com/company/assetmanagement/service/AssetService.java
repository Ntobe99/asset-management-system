package com.company.assetmanagement.service;

import com.company.assetmanagement.dto.AssetAssignmentRequest;
import com.company.assetmanagement.dto.AssetDto;
import com.company.assetmanagement.entity.*;
import com.company.assetmanagement.exception.BadRequestException;
import com.company.assetmanagement.exception.ResourceNotFoundException;
import com.company.assetmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final CategoryRepository categoryRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetAssignmentHistoryRepository historyRepository;

    public Page<AssetDto> search(String term, Long categoryId, AssetStatus status, Long employeeId, Pageable pageable) {
        return assetRepository.findAll(AssetSpecifications.withFilters(term, categoryId, status, employeeId), pageable)
                .map(this::toDto);
    }

    public AssetDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public AssetDto create(AssetDto dto) {
        if (assetRepository.existsByAssetTag(dto.getAssetTag())) {
            throw new BadRequestException("An asset with this tag already exists: " + dto.getAssetTag());
        }
        Category category = findCategory(dto.getCategoryId());

        Asset asset = Asset.builder()
                .assetTag(dto.getAssetTag())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .status(dto.getStatus() != null ? dto.getStatus() : AssetStatus.AVAILABLE)
                .location(dto.getLocation())
                .manufacturer(dto.getManufacturer())
                .modelNumber(dto.getModelNumber())
                .serialNumber(dto.getSerialNumber())
                .purchaseDate(dto.getPurchaseDate())
                .purchaseCost(dto.getPurchaseCost())
                .currentValue(dto.getCurrentValue())
                .warrantyExpiry(dto.getWarrantyExpiry())
                .build();

        return toDto(assetRepository.save(asset));
    }

    public AssetDto update(Long id, AssetDto dto) {
        Asset asset = findEntity(id);

        if (!asset.getAssetTag().equals(dto.getAssetTag()) && assetRepository.existsByAssetTag(dto.getAssetTag())) {
            throw new BadRequestException("An asset with this tag already exists: " + dto.getAssetTag());
        }

        Category category = findCategory(dto.getCategoryId());

        asset.setAssetTag(dto.getAssetTag());
        asset.setName(dto.getName());
        asset.setDescription(dto.getDescription());
        asset.setCategory(category);
        asset.setLocation(dto.getLocation());
        asset.setManufacturer(dto.getManufacturer());
        asset.setModelNumber(dto.getModelNumber());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setPurchaseCost(dto.getPurchaseCost());
        asset.setCurrentValue(dto.getCurrentValue());
        asset.setWarrantyExpiry(dto.getWarrantyExpiry());

        if (dto.getStatus() != null) {
            asset.setStatus(dto.getStatus());
        }

        return toDto(assetRepository.save(asset));
    }

    public void delete(Long id) {
        Asset asset = findEntity(id);
        assetRepository.delete(asset);
    }

    public AssetDto assign(Long assetId, AssetAssignmentRequest request) {
        Asset asset = findEntity(assetId);

        if (asset.getStatus() == AssetStatus.RETIRED || asset.getStatus() == AssetStatus.DISPOSED) {
            throw new BadRequestException("Cannot assign a retired or disposed asset");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        asset.setAssignedEmployee(employee);
        asset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        AssetAssignmentHistory history = AssetAssignmentHistory.builder()
                .asset(asset)
                .employee(employee)
                .assignedAt(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        historyRepository.save(history);

        return toDto(asset);
    }

    public AssetDto unassign(Long assetId) {
        Asset asset = findEntity(assetId);

        if (asset.getAssignedEmployee() == null) {
            throw new BadRequestException("Asset is not currently assigned to anyone");
        }

        asset.setAssignedEmployee(null);
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        return toDto(asset);
    }

    public AssetDto updateStatus(Long assetId, AssetStatus status) {
        Asset asset = findEntity(assetId);
        if (status == AssetStatus.AVAILABLE) {
            asset.setAssignedEmployee(null);
        }
        asset.setStatus(status);
        return toDto(assetRepository.save(asset));
    }

    private Asset findEntity(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private AssetDto toDto(Asset asset) {
        return AssetDto.builder()
                .id(asset.getId())
                .assetTag(asset.getAssetTag())
                .name(asset.getName())
                .description(asset.getDescription())
                .categoryId(asset.getCategory() != null ? asset.getCategory().getId() : null)
                .categoryName(asset.getCategory() != null ? asset.getCategory().getName() : null)
                .status(asset.getStatus())
                .location(asset.getLocation())
                .manufacturer(asset.getManufacturer())
                .modelNumber(asset.getModelNumber())
                .serialNumber(asset.getSerialNumber())
                .purchaseDate(asset.getPurchaseDate())
                .purchaseCost(asset.getPurchaseCost())
                .currentValue(asset.getCurrentValue())
                .warrantyExpiry(asset.getWarrantyExpiry())
                .assignedEmployeeId(asset.getAssignedEmployee() != null ? asset.getAssignedEmployee().getId() : null)
                .assignedEmployeeName(asset.getAssignedEmployee() != null
                        ? asset.getAssignedEmployee().getFirstName() + " " + asset.getAssignedEmployee().getLastName()
                        : null)
                .build();
    }
}

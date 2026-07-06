package com.company.assetmanagement.service;

import com.company.assetmanagement.dto.MaintenanceRecordDto;
import com.company.assetmanagement.entity.*;
import com.company.assetmanagement.exception.BadRequestException;
import com.company.assetmanagement.exception.ResourceNotFoundException;
import com.company.assetmanagement.repository.AssetRepository;
import com.company.assetmanagement.repository.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final AssetRepository assetRepository;

    public Page<MaintenanceRecordDto> getAll(Long assetId, Pageable pageable) {
        Page<MaintenanceRecord> page = assetId != null
                ? maintenanceRecordRepository.findByAssetId(assetId, pageable)
                : maintenanceRecordRepository.findAll(pageable);
        return page.map(this::toDto);
    }

    public MaintenanceRecordDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public MaintenanceRecordDto create(MaintenanceRecordDto dto) {
        Asset asset = findAsset(dto.getAssetId());

        MaintenanceRecord record = MaintenanceRecord.builder()
                .asset(asset)
                .type(dto.getType())
                .status(dto.getStatus() != null ? dto.getStatus() : MaintenanceStatus.SCHEDULED)
                .description(dto.getDescription())
                .scheduledDate(dto.getScheduledDate())
                .completedDate(dto.getCompletedDate())
                .cost(dto.getCost())
                .performedBy(dto.getPerformedBy())
                .vendor(dto.getVendor())
                .notes(dto.getNotes())
                .build();

        // If this maintenance is starting now, reflect it on the asset's status
        if (record.getStatus() == MaintenanceStatus.IN_PROGRESS) {
            asset.setStatus(AssetStatus.IN_MAINTENANCE);
            assetRepository.save(asset);
        }

        return toDto(maintenanceRecordRepository.save(record));
    }

    public MaintenanceRecordDto update(Long id, MaintenanceRecordDto dto) {
        MaintenanceRecord record = findEntity(id);
        Asset asset = record.getAsset();

        record.setType(dto.getType());
        record.setDescription(dto.getDescription());
        record.setScheduledDate(dto.getScheduledDate());
        record.setCompletedDate(dto.getCompletedDate());
        record.setCost(dto.getCost());
        record.setPerformedBy(dto.getPerformedBy());
        record.setVendor(dto.getVendor());
        record.setNotes(dto.getNotes());

        if (dto.getStatus() != null) {
            record.setStatus(dto.getStatus());

            if (dto.getStatus() == MaintenanceStatus.IN_PROGRESS) {
                asset.setStatus(AssetStatus.IN_MAINTENANCE);
                assetRepository.save(asset);
            } else if (dto.getStatus() == MaintenanceStatus.COMPLETED) {
                if (record.getCompletedDate() == null) {
                    record.setCompletedDate(LocalDate.now());
                }
                if (asset.getStatus() == AssetStatus.IN_MAINTENANCE) {
                    asset.setStatus(asset.getAssignedEmployee() != null ? AssetStatus.ASSIGNED : AssetStatus.AVAILABLE);
                    assetRepository.save(asset);
                }
            }
        }

        return toDto(maintenanceRecordRepository.save(record));
    }

    public void delete(Long id) {
        MaintenanceRecord record = findEntity(id);
        if (record.getStatus() == MaintenanceStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot delete a maintenance record that is in progress. Cancel it first.");
        }
        maintenanceRecordRepository.delete(record);
    }

    private MaintenanceRecord findEntity(Long id) {
        return maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found with id: " + id));
    }

    private Asset findAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }

    private MaintenanceRecordDto toDto(MaintenanceRecord record) {
        return MaintenanceRecordDto.builder()
                .id(record.getId())
                .assetId(record.getAsset().getId())
                .assetName(record.getAsset().getName())
                .assetTag(record.getAsset().getAssetTag())
                .type(record.getType())
                .status(record.getStatus())
                .description(record.getDescription())
                .scheduledDate(record.getScheduledDate())
                .completedDate(record.getCompletedDate())
                .cost(record.getCost())
                .performedBy(record.getPerformedBy())
                .vendor(record.getVendor())
                .notes(record.getNotes())
                .build();
    }
}

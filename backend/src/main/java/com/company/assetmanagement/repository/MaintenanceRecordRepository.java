package com.company.assetmanagement.repository;

import com.company.assetmanagement.entity.MaintenanceRecord;
import com.company.assetmanagement.entity.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    Page<MaintenanceRecord> findByAssetId(Long assetId, Pageable pageable);
    List<MaintenanceRecord> findByStatusAndScheduledDateBefore(MaintenanceStatus status, LocalDate date);
    long countByStatus(MaintenanceStatus status);
}

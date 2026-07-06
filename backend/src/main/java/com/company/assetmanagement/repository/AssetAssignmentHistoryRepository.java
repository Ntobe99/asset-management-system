package com.company.assetmanagement.repository;

import com.company.assetmanagement.entity.AssetAssignmentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetAssignmentHistoryRepository extends JpaRepository<AssetAssignmentHistory, Long> {
    Page<AssetAssignmentHistory> findByAssetIdOrderByAssignedAtDesc(Long assetId, Pageable pageable);
}

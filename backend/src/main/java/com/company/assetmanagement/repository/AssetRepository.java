package com.company.assetmanagement.repository;

import com.company.assetmanagement.entity.Asset;
import com.company.assetmanagement.entity.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {
    boolean existsByAssetTag(String assetTag);
    Optional<Asset> findByAssetTag(String assetTag);
    long countByStatus(AssetStatus status);
    Page<Asset> findByAssignedEmployeeId(Long employeeId, Pageable pageable);
}

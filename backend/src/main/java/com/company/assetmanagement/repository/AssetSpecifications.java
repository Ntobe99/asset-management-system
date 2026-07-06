package com.company.assetmanagement.repository;

import com.company.assetmanagement.entity.Asset;
import com.company.assetmanagement.entity.AssetStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class AssetSpecifications {

    private AssetSpecifications() {
    }

    public static Specification<Asset> withFilters(String search, Long categoryId, AssetStatus status, Long employeeId) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("assetTag")), like),
                        cb.like(cb.lower(root.get("serialNumber")), like),
                        cb.like(cb.lower(root.get("manufacturer")), like)
                ));
            }

            if (categoryId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), categoryId));
            }

            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }

            if (employeeId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("assignedEmployee").get("id"), employeeId));
            }

            return predicates;
        };
    }
}

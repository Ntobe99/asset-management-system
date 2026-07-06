package com.company.assetmanagement.controller;

import com.company.assetmanagement.dto.AssetAssignmentRequest;
import com.company.assetmanagement.dto.AssetDto;
import com.company.assetmanagement.entity.AssetStatus;
import com.company.assetmanagement.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public Page<AssetDto> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) Long employeeId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return assetService.search(search, categoryId, status, employeeId, pageable);
    }

    @GetMapping("/{id}")
    public AssetDto getById(@PathVariable Long id) {
        return assetService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AssetDto> create(@Valid @RequestBody AssetDto dto) {
        return ResponseEntity.ok(assetService.create(dto));
    }

    @PutMapping("/{id}")
    public AssetDto update(@PathVariable Long id, @Valid @RequestBody AssetDto dto) {
        return assetService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign")
    public AssetDto assign(@PathVariable Long id, @Valid @RequestBody AssetAssignmentRequest request) {
        return assetService.assign(id, request);
    }

    @PostMapping("/{id}/unassign")
    public AssetDto unassign(@PathVariable Long id) {
        return assetService.unassign(id);
    }

    @PatchMapping("/{id}/status")
    public AssetDto updateStatus(@PathVariable Long id, @RequestParam AssetStatus status) {
        return assetService.updateStatus(id, status);
    }
}

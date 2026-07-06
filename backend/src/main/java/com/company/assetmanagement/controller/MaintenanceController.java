package com.company.assetmanagement.controller;

import com.company.assetmanagement.dto.MaintenanceRecordDto;
import com.company.assetmanagement.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public Page<MaintenanceRecordDto> getAll(
            @RequestParam(required = false) Long assetId,
            @PageableDefault(size = 20, sort = "scheduledDate") Pageable pageable) {
        return maintenanceService.getAll(assetId, pageable);
    }

    @GetMapping("/{id}")
    public MaintenanceRecordDto getById(@PathVariable Long id) {
        return maintenanceService.getById(id);
    }

    @PostMapping
    public ResponseEntity<MaintenanceRecordDto> create(@Valid @RequestBody MaintenanceRecordDto dto) {
        return ResponseEntity.ok(maintenanceService.create(dto));
    }

    @PutMapping("/{id}")
    public MaintenanceRecordDto update(@PathVariable Long id, @Valid @RequestBody MaintenanceRecordDto dto) {
        return maintenanceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

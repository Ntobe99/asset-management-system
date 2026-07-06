package com.company.assetmanagement.service;

import com.company.assetmanagement.dto.EmployeeDto;
import com.company.assetmanagement.entity.AssetStatus;
import com.company.assetmanagement.entity.Employee;
import com.company.assetmanagement.exception.BadRequestException;
import com.company.assetmanagement.exception.ResourceNotFoundException;
import com.company.assetmanagement.repository.AssetRepository;
import com.company.assetmanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AssetRepository assetRepository;

    public List<EmployeeDto> getAll(String search) {
        List<Employee> employees = StringUtils.hasText(search)
                ? employeeRepository.search(search)
                : employeeRepository.findAll();
        return employees.stream().map(this::toDto).toList();
    }

    public EmployeeDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public EmployeeDto create(EmployeeDto dto) {
        Employee employee = Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .position(dto.getPosition())
                .active(true)
                .build();
        return toDto(employeeRepository.save(employee));
    }

    public EmployeeDto update(Long id, EmployeeDto dto) {
        Employee employee = findEntity(id);
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setActive(dto.isActive());
        return toDto(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        Employee employee = findEntity(id);
        long assignedCount = assetRepository.count((root, query, cb) ->
                cb.equal(root.get("assignedEmployee").get("id"), id));
        if (assignedCount > 0) {
            throw new BadRequestException("Cannot delete an employee who has assets currently assigned to them");
        }
        employeeRepository.delete(employee);
    }

    private Employee findEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeDto toDto(Employee employee) {
        long assignedCount = assetRepository.count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("assignedEmployee").get("id"), employee.getId()),
                        cb.notEqual(root.get("status"), AssetStatus.RETIRED)
                ));
        return EmployeeDto.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .active(employee.isActive())
                .assignedAssetCount(assignedCount)
                .build();
    }
}

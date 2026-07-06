package com.company.assetmanagement.repository;

import com.company.assetmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Employee> search(String term);
}

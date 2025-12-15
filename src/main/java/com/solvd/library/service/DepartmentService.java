package com.solvd.library.service;

import com.solvd.library.domain.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    Department create(Department department);

    Optional<Department> findById(Long id);

    List<Department> findAll();

    void update(Department department);

    void delete(Long id);
}

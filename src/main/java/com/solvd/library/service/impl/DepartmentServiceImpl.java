package com.solvd.library.service.impl;

import com.solvd.library.domain.Department;
import com.solvd.library.persistence.DepartmentRepository;
import com.solvd.library.persistence.impl.DepartmentRepositoryImpl;
import com.solvd.library.service.DepartmentService;

import java.util.List;
import java.util.Optional;

public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository = new DepartmentRepositoryImpl();

    @Override
    public Department create(Department department) {
        departmentRepository.create(department);
        return department;
    }

    @Override
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public void update(Department department) {
        departmentRepository.update(department);
    }

    @Override
    public void delete(Long id) {
        departmentRepository.delete(id);
    }
}

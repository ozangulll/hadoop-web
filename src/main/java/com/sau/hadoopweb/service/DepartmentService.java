package com.sau.hadoopweb.service;

import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department findById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public void updateDepartment(Long id, Department updatedDepartment) {
        Department department = findById(id);
        if (department != null) {
            department.setDName(updatedDepartment.getDName());
            department.setLoc(updatedDepartment.getLoc());
            departmentRepository.save(department);
        }
    }
}

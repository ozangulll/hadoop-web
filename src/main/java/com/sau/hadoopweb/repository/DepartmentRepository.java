package com.sau.hadoopweb.repository;

import com.sau.hadoopweb.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}

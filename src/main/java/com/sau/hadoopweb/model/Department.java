package com.sau.hadoopweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter

public class Department {
    @Id
    private Long id;

    private String dName;
    private String loc;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
    public Department(Long deptNo, String dName, String loc) {
        this.id = deptNo;
        this.dName = dName;
        this.loc = loc;
    }

    public Department() {

    }
}
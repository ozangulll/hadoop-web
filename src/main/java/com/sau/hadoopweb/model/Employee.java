package com.sau.hadoopweb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Nullable;
import java.util.Date;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    private int id;

    private String ename;
    private String job;

    @Column(nullable = true)
    private Integer mgr;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date hireDate;


    private Long sal;

    @Column(nullable = true)
    private Integer comm;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = true)
    private String imagePath;

    public Employee(int empNo, String ename, String job, Integer mgr, Date hireDate, Long sal, Integer comm, Department department, String imagePath) {
        this.id = empNo;
        this.ename = ename;
        this.job = job;
        this.mgr = mgr;
        this.hireDate = hireDate;
        this.sal = sal;
        this.comm = comm;
        this.department = department;
        this.imagePath = imagePath;
    }

    public Employee() {
        // Default constructor
    }


    public String getName() {
        return "";
    }
}

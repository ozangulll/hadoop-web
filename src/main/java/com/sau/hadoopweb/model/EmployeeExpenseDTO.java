package com.sau.hadoopweb.model;

import java.util.Date;

public class EmployeeExpenseDTO {
    private long id;
    private String ename;
    private String job;
    private String mgr;
    private Date hireDate;
    private double sal;
    private double comm;
    private String departmentName;
    private String imagePath;
    private double totalExpense;
    public EmployeeExpenseDTO(long id, String ename, String job, Integer mgr, Date hireDate,
                              double sal, double comm, String departmentName, String imagePath, double totalExpense) {
        this.id = id;
        this.ename = ename;
        this.job = job;
        this.mgr = String.valueOf(mgr);
        this.hireDate = hireDate;
        this.sal = sal;
        this.comm = comm;
        this.departmentName = departmentName;
        this.imagePath = imagePath;
        this.totalExpense = totalExpense;
    }

    // Getter ve Setter metodları
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMgr() {
        return mgr;
    }

    public void setMgr(String mgr) {
        this.mgr = mgr;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public double getSal() {
        return sal;
    }

    public void setSal(double sal) {
        this.sal = sal;
    }

    public double getComm() {
        return comm;
    }

    public void setComm(double comm) {
        this.comm = comm;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }
}

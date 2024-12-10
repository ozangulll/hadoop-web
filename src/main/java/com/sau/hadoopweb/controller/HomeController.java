package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.model.EmployeeExpenseDTO;
import com.sau.hadoopweb.model.ExpenseSummary;
import com.sau.hadoopweb.repository.EmployeeRepository;
import com.sau.hadoopweb.repository.DepartmentRepository;
import com.sau.hadoopweb.service.SparkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller

public class HomeController {
    private final SparkService sparkService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public HomeController(SparkService sparkService,
                          EmployeeRepository employeeRepository,
                          DepartmentRepository departmentRepository) {
        this.sparkService = sparkService;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // Method to serve the home page with employee and department data
    @GetMapping("/")
    public String home(Model model) {
        // Employee ve Department verilerini al
        List<Employee> employees = employeeRepository.findAll();
        List<Department> departments = departmentRepository.findAll();

        // Debug: Employee ve Department sayıları yazdır
        System.out.println("Employee count: " + employees.size());
        System.out.println("Department count: " + departments.size());

        model.addAttribute("departments", departments);

        // Harcama özetlerini al
        List<ExpenseSummary> expenseSummaryList = sparkService.getTotalExpensesByUser();

        // Debug: Harcama özetlerinin sayısını yazdır
        System.out.println("ExpenseSummary count: " + expenseSummaryList.size());
        for (ExpenseSummary expenseSummary : expenseSummaryList) {
            System.out.println("UserId: " + expenseSummary.getUserId() + " - TotalExpense: " + expenseSummary.getTotalExpenses());
        }

        // Employee ve ExpenseSummary verilerini DTO ile birleştirmek için Map oluştur
        Map<Long, Double> expenseSummaryMap = new HashMap<>();
        for (ExpenseSummary expenseSummary : expenseSummaryList) {
            expenseSummaryMap.put(expenseSummary.getUserId(), expenseSummary.getTotalExpenses());
        }

        // DTO'yu oluşturacak listeyi başlat
        List<EmployeeExpenseDTO> employeeExpenseDTOList = new ArrayList<>();

        // Employee verilerini işleyip DTO listesine ekle
        for (Employee employee : employees) {
            // Debug: Employee ID'sini yazdır
            System.out.println("Processing Employee ID: " + employee.getId());

            // Employee ile eşleşen harcama verisini al
            double totalExpense = expenseSummaryMap.getOrDefault((long) employee.getId(), 0.0);

            // Debug: Employee'ye ait harcama bilgisini yazdır
            System.out.println("TotalExpense for Employee ID " + employee.getId() + ": " + totalExpense);

            // Null kontrolü ve comm değerinin 0.0 olarak ayarlanması
            double comm = (employee.getComm() != null) ? employee.getComm() : 0.0;

            // DTO oluştur
            EmployeeExpenseDTO dto = new EmployeeExpenseDTO(
                    employee.getId(),
                    employee.getEname(),
                    employee.getJob(),
                    employee.getMgr(),
                    employee.getHireDate(),
                    employee.getSal(),
                    comm, // comm değerini burada kullanalım
                    employee.getDepartment() != null ? employee.getDepartment().getDName() : "No Department", // Null kontrolü
                    employee.getImagePath(),
                    totalExpense
            );

            // DTO'yu listeye ekle
            employeeExpenseDTOList.add(dto);
        }

        // Model'e DTO listesini ekle
        model.addAttribute("employeeExpenseDTOList", employeeExpenseDTOList);

        // Debug: Sonuç olarak gönderilen DTO'ların sayısını yazdır
        System.out.println("EmployeeExpenseDTO list size: " + employeeExpenseDTOList.size());

        return "home"; // home.html (view name)
    }



    // Method to return total expenses by user
    @GetMapping("/total-by-user")
    public String getTotalExpensesByUser(Model model) {
        List<ExpenseSummary> expenseSummaryList = sparkService.getTotalExpensesByUser();
        model.addAttribute("expenseSummaryList", expenseSummaryList);
        return "totalExpenses"; // totalExpenses.html (view name)
    }
    @GetMapping("/home")
    public String homeT(Model model) {
        return home(model); // Call home method
    }

}

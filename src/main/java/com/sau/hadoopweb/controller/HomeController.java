package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.Reader.CSVReader;
import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.repository.DepartmentRepository;
import com.sau.hadoopweb.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private final CSVReader csvReader;

    @Autowired
    public HomeController(CSVReader csvReader) {
        this.csvReader = csvReader;
    }

    @GetMapping("/home")
    public String home(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        List<Department> departments = departmentRepository.findAll();

        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);

        return "home";
    }
    @GetMapping("/load-csv")
    public String loadCsv() {
        String employeeCsvFilePath = "src/main/resources/static/emp.csv"; // Set your actual path
        String departmentCsvFilePath = "src/main/resources/static/dept.csv"; // Set your actual path
        try {
            csvReader.readAndInsertEmployees(employeeCsvFilePath, departmentCsvFilePath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            // Handle error (maybe add a flash attribute to show a message)
        }
        return "redirect:/home"; // Redirect to home after loading CSV
    }

}

package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.repository.EmployeeRepository;
import com.sau.hadoopweb.repository.DepartmentRepository;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private static final String UPLOAD_DIR = "/user/ozangul/images/";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Yeni Employee ekleme formunu gösterme
    @GetMapping("/add-employee")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentRepository.findAll());
        return "add-employee"; // add-employee.html
    }

    // Yeni Employee ekleme işlemi
    @PostMapping("/add-employee")
    public String addEmployee(@ModelAttribute Employee employee,
                              @RequestParam("photo") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        String fileName = file.getOriginalFilename();
        employee.setImagePath(fileName);

        // Set up Hadoop Configuration
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000/");
        conf.set("hadoop.security.authentication", "simple");

        try {
            FileSystem fileSystem = FileSystem.get(conf);
            Path hdfsWritePath = new Path(UPLOAD_DIR + fileName);

            // Use try-with-resources for automatic resource management
            try (FSDataOutputStream fsDataOutputStream = fileSystem.create(hdfsWritePath, true);
                 InputStream inputStream = file.getInputStream()) {
                IOUtils.copy(inputStream, fsDataOutputStream);
            }

            // Save the employee record to the database
            employeeRepository.save(employee);
            redirectAttributes.addFlashAttribute("success", "Employee added successfully!");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload photo: " + e.getMessage());
            return "redirect:/add-employee"; // Redirect back to the form on error
        }

        return "redirect:/home"; // Redirect to the home page after success
    }

    @GetMapping("/edit-employee/{id}")
    public String showEditEmployeeForm(@PathVariable("id") Long id, Model model) {
        // Fetch the employee by ID
        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee != null) {
            model.addAttribute("employee", employee);
            model.addAttribute("departments", departmentRepository.findAll());
            return "edit-employee"; // edit-employee.html
        } else {
            return "redirect:/employees"; // Redirect to employee list if not found
        }
    }

    // Process the updated employee data
    @PostMapping("/edit-employee/{id}")
    public String editEmployee(@PathVariable("id") Long id, @ModelAttribute Employee employee,
                               @RequestParam("photo") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        // Fetch the existing employee record
        Employee existingEmployee = employeeRepository.findById(id).orElse(null);

        if (existingEmployee != null) {
            // Update employee details
            existingEmployee.setEname(employee.getEname());
            existingEmployee.setJob(employee.getJob());
            existingEmployee.setMgr(employee.getMgr());
            existingEmployee.setHireDate(employee.getHireDate());
            existingEmployee.setSal(employee.getSal());
            existingEmployee.setComm(employee.getComm());
            existingEmployee.setDepartment(employee.getDepartment());

            // Check if a new photo is uploaded
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                existingEmployee.setImagePath(fileName);

                // Set up Hadoop Configuration
                Configuration conf = new Configuration();
                conf.set("fs.defaultFS", "hdfs://localhost:9000/");
                conf.set("hadoop.security.authentication", "simple");

                try (FileSystem fileSystem = FileSystem.get(conf);
                     InputStream inputStream = file.getInputStream();
                     FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(UPLOAD_DIR + fileName), true)) {
                    IOUtils.copy(inputStream, fsDataOutputStream);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Failed to upload photo: " + e.getMessage());
                    return "redirect:/employees/edit-employee/" + id;
                }
            }

            // Save the updated employee record
            employeeRepository.save(existingEmployee);
            redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Employee not found.");
        }

        return "redirect:/home"; // Redirect to the employee list after update
    }

    // Employee silme işlemi
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        employeeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully");
        return "redirect:/home";
    }
}




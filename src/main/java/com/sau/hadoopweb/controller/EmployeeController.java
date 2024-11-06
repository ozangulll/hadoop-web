package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.repository.EmployeeRepository;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.hadoop.fs.Path;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.nio.file.Paths;

@Controller
public class EmployeeController {

    private static String UPLOAD_DIR = "/user/ozangul/images/";

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/add-employee")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee"; // Thymeleaf template name
    }

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
}
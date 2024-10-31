package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class EmployeeController {

    private static String UPLOAD_DIR = "src/main/resources/static/";

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
        // Handle the image upload
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            try {
                Files.createDirectories(path.getParent()); // Create directories if they don't exist
                file.transferTo(path);
                employee.setImagePath(path.toString()); // Store the file path in the Employee entity
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Failed to upload photo.");
                return "redirect:/add-employee";
            }
        }

        employeeRepository.save(employee);
        redirectAttributes.addFlashAttribute("message", "Employee added successfully.");
        return "redirect:/employees"; // Redirect to employee list after saving
    }
}

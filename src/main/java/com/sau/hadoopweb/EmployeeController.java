package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private final String UPLOAD_DIR = "uploads/"; // Directory to store uploaded files

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "employee/list";
    }

    @GetMapping("/new")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/form";
    }

    @PostMapping
    public String addEmployee(@ModelAttribute("employee") Employee employee,
                              @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String imagePath = UPLOAD_DIR + file.getOriginalFilename();
            employee.setImagePath(imagePath);
            saveFile(file);
        }
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Optional<Employee> employee = employeeService.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employee/form";
        }
        return "redirect:/employees";
    }

    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable("id") int id,
                                 @ModelAttribute("employee") Employee employee,
                                 @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String imagePath = UPLOAD_DIR + file.getOriginalFilename();
            employee.setImagePath(imagePath);
            saveFile(file);
        }
        employee.setId(id);
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        employeeService.deleteById(id);
        return "redirect:/employees";
    }

    private void saveFile(MultipartFile file) {
        try {
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.createDirectories(path.getParent()); // Create the directory if it doesn't exist
            file.transferTo(path); // Save the file
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
}

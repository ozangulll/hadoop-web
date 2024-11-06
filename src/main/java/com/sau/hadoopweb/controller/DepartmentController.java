package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.repository.DepartmentRepository;
import com.sau.hadoopweb.repository.EmployeeRepository;
import com.sau.hadoopweb.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentService departmentService;

    // Departmanları listele
    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "departments"; // departments.html
    }

    @GetMapping("/add-department")
    public String showAddDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "add-department"; // add-department.html
    }

    // Departman ekleme
    @PostMapping("/add-department")
    public String addDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("message", "Department added successfully");
        return "redirect:/home"; // Departmanlar sayfasına yönlendirme
    }

    @GetMapping("/edit-department/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Department department = departmentService.findById(id);
        model.addAttribute("department", department);
        return "edit-department";
    }

    @PostMapping("/edit-department/{id}")
    public String updateDepartment(@PathVariable("id") Long id, @ModelAttribute("department") Department updatedDepartment) {
        departmentService.updateDepartment(id, updatedDepartment);
        return "redirect:/home";
    }

    @GetMapping("/delete-department/{id}")
    public String deleteDepartment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Add error message to redirect attributes
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete department. It is still referenced by employees.");
            return "redirect:/home"; // Redirect to the home page
        }
        return "redirect:/home"; // Redirect to home if deletion was successful
    }

}

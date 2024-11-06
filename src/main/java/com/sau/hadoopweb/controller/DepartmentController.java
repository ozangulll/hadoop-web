package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    // Departmanları listele
    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "departments"; // departments.html
    }

    // Departman ekleme sayfası
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
        return "redirect:/departments"; // Departmanlar sayfasına yönlendirme
    }

    // Departman düzenleme sayfası
    @GetMapping("/edit-department/{id}")
    public String editDepartment(@PathVariable("id") Long id, Model model) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department Id:" + id));
        model.addAttribute("department", department);
        return "edit-department"; // edit-department.html
    }

    // Departman güncelleme
    @PostMapping("/update-department")
    public String updateDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("message", "Department updated successfully");
        return "redirect:/home"; // Departmanlar sayfasına yönlendirme
    }

    // Departman silme
    @GetMapping("/delete-department/{id}")
    public String deleteDepartment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        departmentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Department deleted successfully");
        return "redirect:/departments"; // Departmanlar sayfasına yönlendirme
    }
}

package com.sau.hadoopweb.Reader;

import com.sau.hadoopweb.model.Department;
import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.repository.DepartmentRepository;
import com.sau.hadoopweb.repository.EmployeeRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Locale;

@Service
public class CSVReader {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    @Transactional
    public void readAndInsertEmployees(String employeeCsvFilePath, String departmentCsvFilePath) throws IOException, ParseException {
        loadDepartmentsFromCSV(departmentCsvFilePath);
        updateEmployeesFromCSV(employeeCsvFilePath);
    }

    private void loadDepartmentsFromCSV(String filePath) throws IOException {
        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader("deptno", "dname", "loc").withSkipHeaderRecord())) {

            for (CSVRecord record : csvParser) {
                Long deptNo = Long.parseLong(record.get("deptno"));
                String dName = record.get("dname");
                String loc = record.get("loc");

                Department department = new Department(deptNo, dName, loc);
                departmentRepository.save(department);
            }
        }
    }

    private void updateEmployeesFromCSV(String filePath) throws IOException, ParseException {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord record : csvParser) {
                System.out.println("Record: " + record);
                int empNo = Integer.parseInt(record.get("empno"));
                String ename = record.get("ename").isEmpty() ? null : record.get("ename");
                String job = record.get("job").isEmpty() ? null : record.get("job");
                Integer mgr = record.get("mgr").isEmpty() ? null : Integer.parseInt(record.get("mgr"));
                Date hireDate = dateFormat.parse(record.get("hiredate"));
                Long sal = Long.parseLong(record.get("sal"));
                Integer comm = record.get("comm").isEmpty() ? null : Integer.parseInt(record.get("comm"));
                String imagePath = record.get("img").isEmpty() ? null : record.get("img");
                Long deptNo = Long.parseLong(record.get("deptno"));

                // Fetch the existing department
                Optional<Department> optionalDepartment = departmentRepository.findById(deptNo);
                if (!optionalDepartment.isPresent()) {
                    System.out.println("Department with ID " + deptNo + " not found for employee " + empNo);
                    continue; // Or handle as needed
                }
                Department department = optionalDepartment.get();

                // Create or update the Employee object
                Employee employee = new Employee(empNo, ename, job, mgr, hireDate, sal, comm, department, imagePath);
                editEmployee(employee); // Update the employee
            }
        }
    }

    public void editEmployee(Employee updatedEmployee) {
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById((long) updatedEmployee.getId());
        if (existingEmployeeOpt.isPresent()) {
            Employee existingEmployee = existingEmployeeOpt.get();
            existingEmployee.setEname(updatedEmployee.getName());
            existingEmployee.setJob(updatedEmployee.getJob());
            existingEmployee.setMgr(updatedEmployee.getMgr());
            existingEmployee.setHireDate(updatedEmployee.getHireDate());
            existingEmployee.setSal(updatedEmployee.getSal());
            existingEmployee.setComm(updatedEmployee.getComm());
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
            existingEmployee.setImagePath(updatedEmployee.getImagePath());
            employeeRepository.save(existingEmployee);
        } else {
            System.out.println("Employee with ID " + updatedEmployee.getId() + " not found.");
        }
    }

    public void deleteEmployee(Long employeeId) {
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeId);
        if (existingEmployeeOpt.isPresent()) {
            employeeRepository.delete(existingEmployeeOpt.get());
        } else {
            System.out.println("Employee with ID " + employeeId + " not found.");
        }
    }
}

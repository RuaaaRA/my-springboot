package com.example.payroll.employeeService;

import com.example.payroll.departmentService.Department;
import com.example.payroll.security.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.payroll.security.UserRepository;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeDTO employeeDTO, Department dep, 
    PasswordEncoder passwordEncoder, UserRepository userRepository) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setRole(employeeDTO.getRole());
        employee.setEmail(employeeDTO.getEmail());
        employee.setDepartment(dep);

        // إنشاء مستخدم افتراضي
        String username = employeeDTO.getEmail();
        String password = passwordEncoder.encode("123456");
        String role = "ROLE_USER";

        User user = new User(username, password, role);
        userRepository.save(user); // نحفظ المستخدم أولاً
        employee.setUser(user);    // نربطه بالموظف

        return employee;
    }

    public static EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setName(employee.getName());
        employeeDTO.setRole(employee.getRole());
        employeeDTO.setEmail(employee.getEmail());
    
        if (employee.getDepartment() != null) {
            employeeDTO.setDepartmentName(employee.getDepartment().getName());
        }
    
        return employeeDTO;
    }
}

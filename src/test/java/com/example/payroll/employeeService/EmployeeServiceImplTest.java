package com.example.payroll.employeeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.payroll.departmentService.DepartmentRepository;
import com.example.payroll.security.User;
import com.example.payroll.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeModelAssembler assembler;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setup() {
        // mock security context
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFindById_AccessAllowed() {
        // Arrange
        Long employeeId = 1L;
        String email = "user@example.com";
        String role = "ROLE_USER";

        // mock user
        User mockUser = new User(email, "password", role);
        mockUser.setId(1L);

        // mock employee
        Employee mockEmployee = new Employee("Test Name", "Engineer", email);
        mockEmployee.setId(employeeId);
        mockEmployee.setUser(mockUser);

        // mock dto and assembler
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setName("Test Name");
        employeeDTO.setEmail(email);
        employeeDTO.setRole("Engineer");

        EntityModel<EmployeeDTO> model = EntityModel.of(employeeDTO);

        // mock behavior
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        when(userRepository.findByUsername(email)).thenReturn(Optional.of(mockUser));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mockEmployee));
        when(assembler.toModel(any(EmployeeDTO.class))).thenReturn(model);

        // Act
        ResponseEntity<?> response = employeeService.findById(employeeId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(model, response.getBody());
    }
}

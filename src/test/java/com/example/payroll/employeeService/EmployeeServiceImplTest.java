package com.example.payroll;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.example.payroll.employeeService.Employee;
import com.example.payroll.employeeService.EmployeeController;
import com.example.payroll.employeeService.EmployeeDTO;
import com.example.payroll.employeeService.EmployeeModelAssembler;
import com.example.payroll.employeeService.EmployeeRepository;
import com.example.payroll.employeeService.EmployeeServiceImpl;
import com.example.payroll.security.User;
import com.example.payroll.security.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@SpringBootTest
class EmployeeTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeModelAssembler assembler;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Employee emp = new Employee("John Doe", "Developer", "john@example.com");
        emp.setId(1L);
        List<Employee> list = List.of(emp);

        when(employeeRepository.findAll()).thenReturn(list);
        when(assembler.toModel(any())).thenReturn(EntityModel.of(new EmployeeDTO()));

        CollectionModel<EntityModel<EmployeeDTO>> result = employeeService.findAll();
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testNewEmployee() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("new@example.com");
        dto.setName("New");
        dto.setRole("Dev");
        dto.setUsername("newuser");
        dto.setPassword("pass");

        User user = new User();
        user.setId(1L);
        user.setUsername("newuser");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass")).thenReturn("hashedpass");

        Employee savedEmployee = new Employee("New", "Dev", "new@example.com");
        savedEmployee.setId(1L);
        savedEmployee.setUser(user);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        EntityModel<EmployeeDTO> modelWithSelfLink = EntityModel.of(dto,
                linkTo(methodOn(EmployeeController.class).one(10L)).withSelfRel());

        when(assembler.toModel(any())).thenReturn(modelWithSelfLink);

        ResponseEntity<?> response = employeeService.newEmployee(dto);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testFindById_AsAdmin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole("ROLE_ADMIN");

        Employee emp = new Employee("Admin", "Manager", "admin@example.com");
        emp.setId(2L);
        emp.setUser(user);

        mockSecurityContext(user);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(emp));
        when(assembler.toModel(any())).thenReturn(EntityModel.of(new EmployeeDTO()));

        ResponseEntity<?> response = employeeService.findById(2L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testFindById_Forbidden() {
        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("user1");
        authUser.setRole("ROLE_USER");

        User empUser = new User();
        empUser.setId(2L);

        Employee emp = new Employee("Someone", "Role", "email@example.com");
        emp.setId(3L);
        emp.setUser(empUser);

        mockSecurityContext(authUser);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(authUser));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(emp));

        ResponseEntity<?> response = employeeService.findById(3L);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testFindByEmail_Success() {
        Employee emp = new Employee("Jane", "Tester", "jane@example.com");
        emp.setId(5L);

        when(employeeRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(emp));
        when(assembler.toModel(any())).thenReturn(EntityModel.of(new EmployeeDTO()));

        EntityModel<EmployeeDTO> model = employeeService.findByEmail("jane@example.com");
        assertNotNull(model);
    }

    @Test
    void testSave_UpdateExisting() {
        Employee existing = new Employee("Old", "OldRole", "old@example.com");
        existing.setId(10L);

        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("NewName");
        dto.setRole("NewRole");
        dto.setEmail("new@example.com");

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any())).thenReturn(existing);
        EntityModel<EmployeeDTO> modelWithSelfLink = EntityModel.of(dto,
                linkTo(methodOn(EmployeeController.class).one(10L)).withSelfRel());

        when(assembler.toModel(any())).thenReturn(modelWithSelfLink);

        ResponseEntity<?> response = employeeService.save(dto, 10L);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testDeleteById() {
        doNothing().when(employeeRepository).deleteById(99L);
        ResponseEntity<?> response = employeeService.deleteById(99L);
        assertEquals(204, response.getStatusCodeValue());
    }

    private void mockSecurityContext(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getUsername());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
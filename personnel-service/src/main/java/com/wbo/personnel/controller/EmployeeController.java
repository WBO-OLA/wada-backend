package com.wbo.personnel.controller;

import com.wbo.common.dto.ApiResponse;
import com.wbo.personnel.entity.Employee;
import com.wbo.personnel.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personnel/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(ApiResponse.ok("Employee created", employeeService.create(employee)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> update(@PathVariable Long id, @RequestBody Employee employee) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated", employeeService.update(id, employee)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted", null));
    }
}

package com.dormitory.system.controller;

import com.dormitory.system.entity.Maintenance;
import com.dormitory.system.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
public class MaintenanceController {
    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping
    public List<Maintenance> getAll(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return maintenanceService.findByStatus(status);
        }
        return maintenanceService.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Maintenance> getByStudent(@PathVariable Long studentId, @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return maintenanceService.findByStudentIdAndStatus(studentId, status);
        }
        return maintenanceService.findByStudentId(studentId);
    }

    @GetMapping("/{id}")
    public Maintenance getById(@PathVariable Long id) {
        return maintenanceService.findById(id);
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance m) {
        return maintenanceService.create(m);
    }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id, @RequestBody Maintenance m) {
        return maintenanceService.update(id, m);
    }

    @PutMapping("/{id}/status")
    public Maintenance updateStatus(@PathVariable Long id, @RequestParam String status) {
        return maintenanceService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        maintenanceService.delete(id);
    }
}

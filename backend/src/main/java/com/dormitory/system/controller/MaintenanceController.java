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
            return maintenanceService.getMaintenancesByStatus(status);
        }
        return maintenanceService.getAllMaintenances();
    }

    @GetMapping("/student/{studentId}")
    public List<Maintenance> getByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return maintenanceService.getMaintenancesByStudentAndStatus(studentId, status);
        }
        return maintenanceService.getMaintenancesByStudent(studentId);
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance m, @RequestParam(required = false) Long studentId) {
        if (studentId != null) {
            return maintenanceService.createMaintenance(m, studentId);
        }
        if (m.getStudent() != null && m.getStudent().getId() != null) {
            return maintenanceService.createMaintenance(m, m.getStudent().getId());
        }
        throw new RuntimeException("Student ID is required");
    }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id, @RequestBody Maintenance m) {
        return maintenanceService.updateMaintenance(id, m);
    }

    @PutMapping("/{id}/status")
    public Maintenance updateStatus(@PathVariable Long id, @RequestParam String status) {
        return maintenanceService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
    }
}

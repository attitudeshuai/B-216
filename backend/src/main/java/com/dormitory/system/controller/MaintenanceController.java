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
    public List<Maintenance> getAll() {
        return maintenanceService.getAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Maintenance> getByStudent(@PathVariable Long studentId) {
        return maintenanceService.getByStudentId(studentId);
    }

    @GetMapping("/status/{status}")
    public List<Maintenance> getByStatus(@PathVariable String status) {
        return maintenanceService.getByStatus(status);
    }

    @GetMapping("/{id}")
    public Maintenance getById(@PathVariable Long id) {
        return maintenanceService.getById(id);
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance maintenance) {
        return maintenanceService.create(maintenance);
    }

    @PutMapping("/{id}/status")
    public Maintenance updateStatus(@PathVariable Long id, @RequestParam String status) {
        return maintenanceService.updateStatus(id, status);
    }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        return maintenanceService.update(id, maintenance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        maintenanceService.delete(id);
    }
}

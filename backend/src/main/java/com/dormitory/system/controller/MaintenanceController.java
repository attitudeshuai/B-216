package com.dormitory.system.controller;

import com.dormitory.system.entity.Maintenance;
import com.dormitory.system.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
public class MaintenanceController {
    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @GetMapping
    public List<Maintenance> getAll() { return maintenanceRepository.findAll(); }

    @GetMapping("/student/{studentId}")
    public List<Maintenance> getByStudent(@PathVariable Long studentId) {
        return maintenanceRepository.findByStudentId(studentId);
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance m) { return maintenanceRepository.save(m); }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id, @RequestBody Maintenance m) {
        m.setId(id);
        return maintenanceRepository.save(m);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { maintenanceRepository.deleteById(id); }
}

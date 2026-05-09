package com.dormitory.system.controller;

import com.dormitory.system.entity.DormitoryApplication;
import com.dormitory.system.repository.DormitoryApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    @Autowired
    private DormitoryApplicationRepository applicationRepository;

    @GetMapping
    public List<DormitoryApplication> getAll() {
        return applicationRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<DormitoryApplication> getByStudent(@PathVariable Long studentId) {
        return applicationRepository.findByStudentId(studentId);
    }

    @PostMapping
    public DormitoryApplication create(@RequestBody DormitoryApplication app) {
        return applicationRepository.save(app);
    }

    @PutMapping("/{id}/status")
    public DormitoryApplication updateStatus(@PathVariable Long id, @RequestParam String status) {
        DormitoryApplication app = applicationRepository.findById(id).orElseThrow(() -> new RuntimeException("申请记录未找到"));
        app.setStatus(status);
        return applicationRepository.save(app);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        applicationRepository.deleteById(id);
    }
}

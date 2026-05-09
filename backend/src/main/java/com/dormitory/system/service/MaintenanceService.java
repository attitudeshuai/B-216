package com.dormitory.system.service;

import com.dormitory.system.entity.Maintenance;
import com.dormitory.system.entity.User;
import com.dormitory.system.repository.MaintenanceRepository;
import com.dormitory.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Maintenance> getAll() {
        return maintenanceRepository.findAllByOrderByReportTimeDesc();
    }

    public List<Maintenance> getByStudentId(Long studentId) {
        return maintenanceRepository.findByStudentIdOrderByReportTimeDesc(studentId);
    }

    public List<Maintenance> getByStatus(String status) {
        return maintenanceRepository.findByStatus(status);
    }

    public Maintenance getById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("报修记录不存在"));
    }

    public Maintenance create(Maintenance maintenance) {
        if (maintenance.getStudent() == null || maintenance.getStudent().getId() == null) {
            throw new RuntimeException("必须指定申报学生");
        }
        User student = userRepository.findById(maintenance.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        maintenance.setStudent(student);
        maintenance.setStatus("待处理");
        maintenance.setReportTime(LocalDateTime.now());
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance updateStatus(Long id, String status) {
        Maintenance m = getById(id);
        if (!List.of("待处理", "处理中", "已完成").contains(status)) {
            throw new RuntimeException("无效的处理状态: " + status);
        }
        m.setStatus(status);
        if ("已完成".equals(status)) {
            m.setCompleteTime(LocalDateTime.now());
        }
        return maintenanceRepository.save(m);
    }

    public Maintenance update(Long id, Maintenance maintenance) {
        Maintenance existing = getById(id);
        existing.setTitle(maintenance.getTitle());
        existing.setDescription(maintenance.getDescription());
        existing.setFaultType(maintenance.getFaultType());
        if (maintenance.getStatus() != null) {
            existing.setStatus(maintenance.getStatus());
        }
        if ("已完成".equals(maintenance.getStatus())) {
            existing.setCompleteTime(LocalDateTime.now());
        }
        if (maintenance.getResult() != null) {
            existing.setResult(maintenance.getResult());
        }
        return maintenanceRepository.save(existing);
    }

    public void delete(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new RuntimeException("报修记录不存在");
        }
        maintenanceRepository.deleteById(id);
    }
}

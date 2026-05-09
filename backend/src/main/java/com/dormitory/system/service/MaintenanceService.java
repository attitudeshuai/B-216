package com.dormitory.system.service;

import com.dormitory.system.entity.Maintenance;
import com.dormitory.system.entity.User;
import com.dormitory.system.repository.MaintenanceRepository;
import com.dormitory.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Maintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> getMaintenancesByStudent(Long studentId) {
        return maintenanceRepository.findByStudentId(studentId);
    }

    public List<Maintenance> getMaintenancesByStatus(String status) {
        return maintenanceRepository.findByStatus(status);
    }

    public List<Maintenance> getMaintenancesByStudentAndStatus(Long studentId, String status) {
        return maintenanceRepository.findByStudentIdAndStatus(studentId, status);
    }

    @Transactional
    public Maintenance createMaintenance(Maintenance maintenance, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        maintenance.setStudent(student);
        maintenance.setStatus("PENDING");
        maintenance.setReportTime(LocalDateTime.now());
        
        if (maintenance.getEmergencyLevel() == null) {
            maintenance.setEmergencyLevel("一般");
        }
        
        return maintenanceRepository.save(maintenance);
    }

    @Transactional
    public Maintenance updateStatus(Long id, String status) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
        
        maintenance.setStatus(status);
        
        if ("DONE".equals(status)) {
            maintenance.setCompleteTime(LocalDateTime.now());
        }
        
        return maintenanceRepository.save(maintenance);
    }

    @Transactional
    public Maintenance updateMaintenance(Long id, Maintenance maintenance) {
        Maintenance existing = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
        
        if (maintenance.getTitle() != null) {
            existing.setTitle(maintenance.getTitle());
        }
        if (maintenance.getDescription() != null) {
            existing.setDescription(maintenance.getDescription());
        }
        if (maintenance.getEmergencyLevel() != null) {
            existing.setEmergencyLevel(maintenance.getEmergencyLevel());
        }
        if (maintenance.getFaultType() != null) {
            existing.setFaultType(maintenance.getFaultType());
        }
        if (maintenance.getStatus() != null) {
            existing.setStatus(maintenance.getStatus());
            if ("DONE".equals(maintenance.getStatus())) {
                existing.setCompleteTime(LocalDateTime.now());
            }
        }
        if (maintenance.getResult() != null) {
            existing.setResult(maintenance.getResult());
        }
        
        return maintenanceRepository.save(existing);
    }

    @Transactional
    public void deleteMaintenance(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new RuntimeException("Maintenance not found");
        }
        maintenanceRepository.deleteById(id);
    }
}

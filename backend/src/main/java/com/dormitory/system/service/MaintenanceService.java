package com.dormitory.system.service;

import com.dormitory.system.entity.Maintenance;
import com.dormitory.system.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceService {
    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<Maintenance> findAll() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> findByStudentId(Long studentId) {
        return maintenanceRepository.findByStudentId(studentId);
    }

    public List<Maintenance> findByStatus(String status) {
        return maintenanceRepository.findByStatus(status);
    }

    public List<Maintenance> findByStudentIdAndStatus(Long studentId, String status) {
        return maintenanceRepository.findByStudentIdAndStatus(studentId, status);
    }

    public Maintenance findById(Long id) {
        return maintenanceRepository.findById(id).orElse(null);
    }

    public Maintenance create(Maintenance maintenance) {
        maintenance.setStatus("PENDING");
        maintenance.setReportTime(LocalDateTime.now());
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance updateStatus(Long id, String status) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElse(null);
        if (maintenance != null) {
            maintenance.setStatus(status);
            if ("DONE".equals(status)) {
                maintenance.setFinishTime(LocalDateTime.now());
            }
            return maintenanceRepository.save(maintenance);
        }
        return null;
    }

    public Maintenance update(Long id, Maintenance maintenance) {
        Maintenance existing = maintenanceRepository.findById(id).orElse(null);
        if (existing != null) {
            if (maintenance.getTitle() != null) existing.setTitle(maintenance.getTitle());
            if (maintenance.getDescription() != null) existing.setDescription(maintenance.getDescription());
            if (maintenance.getFaultType() != null) existing.setFaultType(maintenance.getFaultType());
            if (maintenance.getEmergencyLevel() != null) existing.setEmergencyLevel(maintenance.getEmergencyLevel());
            if (maintenance.getStatus() != null) {
                existing.setStatus(maintenance.getStatus());
                if ("DONE".equals(maintenance.getStatus())) {
                    existing.setFinishTime(LocalDateTime.now());
                }
            }
            if (maintenance.getResult() != null) existing.setResult(maintenance.getResult());
            return maintenanceRepository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        maintenanceRepository.deleteById(id);
    }
}
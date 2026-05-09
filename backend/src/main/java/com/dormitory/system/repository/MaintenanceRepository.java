package com.dormitory.system.repository;

import com.dormitory.system.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByStudentId(Long studentId);
    List<Maintenance> findByStatus(String status);
    List<Maintenance> findByStudentIdAndStatus(Long studentId, String status);
}

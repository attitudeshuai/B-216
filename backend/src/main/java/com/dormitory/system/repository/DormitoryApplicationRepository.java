package com.dormitory.system.repository;

import com.dormitory.system.entity.DormitoryApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DormitoryApplicationRepository extends JpaRepository<DormitoryApplication, Long> {
    List<DormitoryApplication> findByStudentId(Long studentId);
    List<DormitoryApplication> findByStatus(String status);
}

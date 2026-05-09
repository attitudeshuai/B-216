package com.dormitory.system.repository;

import com.dormitory.system.entity.AccessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccessRecordRepository extends JpaRepository<AccessRecord, Long> {
    List<AccessRecord> findByStudentId(Long studentId);
}

package com.dormitory.system.repository;

import com.dormitory.system.entity.SafetyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyRecordRepository extends JpaRepository<SafetyRecord, Long> {
}

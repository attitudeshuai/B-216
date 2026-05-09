package com.dormitory.system.repository;

import com.dormitory.system.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByStudentId(Long studentId);
}

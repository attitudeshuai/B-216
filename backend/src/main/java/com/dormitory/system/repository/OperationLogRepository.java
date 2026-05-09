package com.dormitory.system.repository;

import com.dormitory.system.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findByOperatorUsername(String username);
}

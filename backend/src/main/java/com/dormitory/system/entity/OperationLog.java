package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_logs")
@Data
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operatorUsername;
    private String operation;
    private String details;
    private String ipAddress;
    private LocalDateTime operateTime;

    @PrePersist
    protected void onCreate() {
        if (operateTime == null) operateTime = LocalDateTime.now();
    }
}

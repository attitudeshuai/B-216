package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_records")
@Data
public class AccessRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String direction; // IN, OUT
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTime;
    
    private String location; // e.g., "Building 1 Main Gate"
    private String method; // CARD, FACE, APP

    @PrePersist
    protected void onCreate() {
        if (accessTime == null) accessTime = LocalDateTime.now();
    }
}

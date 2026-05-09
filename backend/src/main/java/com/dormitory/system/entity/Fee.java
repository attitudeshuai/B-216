package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fees")
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private BigDecimal amount;
    private String type; // 住宿费, 水电费
    private String status; // 已缴, 未缴
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate payDate;
}

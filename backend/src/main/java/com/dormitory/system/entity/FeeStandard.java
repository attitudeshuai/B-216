package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fee_standards")
@Data
public class FeeStandard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Standard 4-Bed Room Fee"
    private Double amount;
    private String unit; // YEAR, SEMESTER, MONTH
    private String description;
}

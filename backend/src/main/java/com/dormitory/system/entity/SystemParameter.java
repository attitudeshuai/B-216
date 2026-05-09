package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "system_parameters")
@Data
public class SystemParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paramKey;
    
    private String paramValue;
    private String description;
}

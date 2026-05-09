package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "buildings")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private Integer floorCount;
    private String description;
}

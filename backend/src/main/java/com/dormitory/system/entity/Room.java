package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    private String roomNumber;
    private String type; // 单人间, 双人间, 四人间
    private Integer capacity;
    private Integer availableBeds;
    private String status; // 空闲, 已满, 维修中
}

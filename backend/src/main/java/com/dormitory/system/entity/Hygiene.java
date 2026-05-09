package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "hygiene_records")
public class Hygiene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;
    
    private Double score;
    private String comments;
    private String inspector;
}

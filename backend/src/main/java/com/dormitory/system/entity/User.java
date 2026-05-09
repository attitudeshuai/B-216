package com.dormitory.system.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    // admin, student, dorm_manager
    private String role;

    private String name;
    private String gender;
    private String phone;
    
    // For students
    private String studentId;
    private String className;
    
    // For staff
    private String staffId;
}

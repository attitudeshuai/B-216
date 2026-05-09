package com.dormitory.system.service;

import com.dormitory.system.entity.*;
import com.dormitory.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormService {
    @Autowired private UserRepository userRepository;
    @Autowired private BuildingRepository buildingRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private AllocationRepository allocationRepository;
    @Autowired private FeeRepository feeRepository;
    @Autowired private MaintenanceRepository maintenanceRepository;
    @Autowired private NoticeRepository noticeRepository;
    @Autowired private SafetyRecordRepository safetyRecordRepository;
    @Autowired private HygieneRepository hygieneRepository;
    @Autowired private AttendanceRepository attendanceRepository;

    // Users
    public List<User> getAllUsers() { return userRepository.findAll(); }
    public User getUserById(Long id) { return userRepository.findById(id).orElse(null); }
    public User saveUser(User user) { return userRepository.save(user); }
    public void deleteUser(Long id) { userRepository.deleteById(id); }

    // Buildings & Rooms
    public List<Building> getAllBuildings() { return buildingRepository.findAll(); }
    public Building saveBuilding(Building b) { return buildingRepository.save(b); }
    public void deleteBuilding(Long id) { buildingRepository.deleteById(id); }
    
    public List<Room> getAllRooms() { return roomRepository.findAll(); }
    public Room saveRoom(Room r) { return roomRepository.save(r); }
    public void deleteRoom(Long id) { roomRepository.deleteById(id); }

    // Allocations
    public List<Allocation> getAllAllocations() { return allocationRepository.findAll(); }
    public Allocation saveAllocation(Allocation a) { return allocationRepository.save(a); }

    // Fees
    public List<Fee> getAllFees() { return feeRepository.findAll(); }
    public Fee saveFee(Fee f) { return feeRepository.save(f); }

    // Maintenance
    public List<Maintenance> getAllMaintenances() { return maintenanceRepository.findAll(); }
    public Maintenance saveMaintenance(Maintenance m) { return maintenanceRepository.save(m); }

    // Notices
    public List<Notice> getAllNotices() { return noticeRepository.findAll(); }
    public Notice saveNotice(Notice n) { return noticeRepository.save(n); }
    
    // Safety
    public List<SafetyRecord> getAllSafetyRecords() { return safetyRecordRepository.findAll(); }
    
    // Hygiene
    public List<Hygiene> getAllHygieneRecords() { return hygieneRepository.findAll(); }
    
    // Attendance
    public List<Attendance> getAllAttendances() { return attendanceRepository.findAll(); }
}

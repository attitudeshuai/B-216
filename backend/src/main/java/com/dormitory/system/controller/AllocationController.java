package com.dormitory.system.controller;

import com.dormitory.system.entity.Allocation;
import com.dormitory.system.entity.Room;
import com.dormitory.system.entity.User;
import com.dormitory.system.repository.AllocationRepository;
import com.dormitory.system.repository.RoomRepository;
import com.dormitory.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {
    @Autowired
    private AllocationRepository allocationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public List<Allocation> getAll() {
        return allocationRepository.findAll();
    }

    @GetMapping("/unallocated-students")
    public List<User> getUnallocatedStudents() {
        List<User> students = userRepository.findByRole("student");
        List<Allocation> allocations = allocationRepository.findAll();
        List<Long> allocatedStudentIds = allocations.stream()
                .map(a -> a.getStudent().getId())
                .collect(Collectors.toList());
        return students.stream()
                .filter(s -> !allocatedStudentIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/available-rooms/{buildingId}")
    public List<Room> getAvailableRooms(@PathVariable Long buildingId) {
        return roomRepository.findByBuildingId(buildingId).stream()
                .filter(r -> r.getAvailableBeds() > 0)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Transactional
    public Allocation create(@RequestBody Allocation allocation) {
        // Validate student
        if (allocation.getStudent() == null || allocation.getStudent().getId() == null) {
            throw new RuntimeException("必须选择学生");
        }
        
        // Check if student already allocated
        boolean isAllocated = allocationRepository.findAll().stream()
                .anyMatch(a -> a.getStudent().getId().equals(allocation.getStudent().getId()));
        if (isAllocated) {
            throw new RuntimeException("该学生已分配宿舍");
        }

        // Validate room
        if (allocation.getRoom() == null || allocation.getRoom().getId() == null) {
            throw new RuntimeException("必须选择房间");
        }
        
        Room room = roomRepository.findById(allocation.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        
        if (room.getAvailableBeds() <= 0) {
            throw new RuntimeException("该房间已满员");
        }

        // Update room capacity
        room.setAvailableBeds(room.getAvailableBeds() - 1);
        if (room.getAvailableBeds() == 0) {
            room.setStatus("满员");
        }
        roomRepository.save(room);

        return allocationRepository.save(allocation);
    }

    @PutMapping("/{id}")
    @Transactional
    public Allocation update(@PathVariable Long id, @RequestBody Allocation allocation) {
        Allocation existing = allocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分配记录不存在"));
        
        // If room changed
        if (!existing.getRoom().getId().equals(allocation.getRoom().getId())) {
            // Restore old room capacity
            Room oldRoom = existing.getRoom();
            oldRoom.setAvailableBeds(oldRoom.getAvailableBeds() + 1);
            if (oldRoom.getStatus().equals("满员")) {
                oldRoom.setStatus("空闲");
            }
            roomRepository.save(oldRoom);

            // Check new room capacity
            Room newRoom = roomRepository.findById(allocation.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("新房间不存在"));
            
            if (newRoom.getAvailableBeds() <= 0) {
                throw new RuntimeException("新房间已满员");
            }

            // Update new room capacity
            newRoom.setAvailableBeds(newRoom.getAvailableBeds() - 1);
            if (newRoom.getAvailableBeds() == 0) {
                newRoom.setStatus("满员");
            }
            roomRepository.save(newRoom);
            
            existing.setRoom(newRoom);
        }
        
        existing.setBedNumber(allocation.getBedNumber());
        return allocationRepository.save(existing);
    }
    
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        Allocation existing = allocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分配记录不存在"));
        
        // Restore room capacity
        Room room = existing.getRoom();
        room.setAvailableBeds(room.getAvailableBeds() + 1);
        if (room.getStatus().equals("满员")) {
            room.setStatus("空闲");
        }
        roomRepository.save(room);

        allocationRepository.deleteById(id);
    }
}

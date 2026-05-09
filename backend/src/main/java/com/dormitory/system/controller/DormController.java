package com.dormitory.system.controller;

import com.dormitory.system.entity.*;
import com.dormitory.system.service.DormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DormController {
    @Autowired
    private DormService dormService;

    // Users
    @GetMapping("/users")
    public List<User> getUsers() { return dormService.getAllUsers(); }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        return dormService.saveUser(user);
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        User existing = dormService.getUserById(id);
        if (existing != null && (user.getPassword() == null || user.getPassword().isEmpty())) {
            user.setPassword(existing.getPassword());
        }
        user.setId(id);
        return dormService.saveUser(user);
    }
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) { dormService.deleteUser(id); }

    // Buildings
    @GetMapping("/buildings")
    public List<Building> getBuildings() { return dormService.getAllBuildings(); }
    @PostMapping("/buildings")
    public Building createBuilding(@RequestBody Building b) { return dormService.saveBuilding(b); }
    @PutMapping("/buildings/{id}")
    public Building updateBuilding(@PathVariable Long id, @RequestBody Building b) {
        b.setId(id);
        return dormService.saveBuilding(b);
    }
    @DeleteMapping("/buildings/{id}")
    public void deleteBuilding(@PathVariable Long id) { dormService.deleteBuilding(id); }

    // Rooms
    @GetMapping("/rooms")
    public List<Room> getRooms() { return dormService.getAllRooms(); }
    @PostMapping("/rooms")
    public Room createRoom(@RequestBody Room r) { return dormService.saveRoom(r); }
    @PutMapping("/rooms/{id}")
    public Room updateRoom(@PathVariable Long id, @RequestBody Room r) {
        r.setId(id);
        return dormService.saveRoom(r);
    }
    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) { dormService.deleteRoom(id); }


    // Allocations - Moved to AllocationController



    // Fees - Moved to FeeController
    // Maintenance - Moved to MaintenanceController
    // Notices - Moved to NoticeController
    // Stats - Moved to StatsController
}


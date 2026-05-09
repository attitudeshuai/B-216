package com.dormitory.system.controller;

import com.dormitory.system.entity.Attendance;
import com.dormitory.system.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping
    public List<Attendance> getAll() { return attendanceRepository.findAll(); }

    @PostMapping
    public Attendance create(@RequestBody Attendance a) { return attendanceRepository.save(a); }

    @PutMapping("/{id}")
    public Attendance update(@PathVariable Long id, @RequestBody Attendance a) {
        a.setId(id);
        return attendanceRepository.save(a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { attendanceRepository.deleteById(id); }
}

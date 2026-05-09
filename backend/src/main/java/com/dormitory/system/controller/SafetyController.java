package com.dormitory.system.controller;

import com.dormitory.system.entity.AccessRecord;
import com.dormitory.system.entity.SafetyRecord;
import com.dormitory.system.repository.AccessRecordRepository;
import com.dormitory.system.repository.SafetyRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SafetyController {
    @Autowired
    private SafetyRecordRepository safetyRecordRepository;
    @Autowired
    private AccessRecordRepository accessRecordRepository;

    // Safety Records
    @GetMapping("/safety")
    public List<SafetyRecord> getSafetyRecords() { return safetyRecordRepository.findAll(); }

    @PostMapping("/safety")
    public SafetyRecord createSafetyRecord(@RequestBody SafetyRecord r) { return safetyRecordRepository.save(r); }

    @PutMapping("/safety/{id}")
    public SafetyRecord updateSafetyRecord(@PathVariable Long id, @RequestBody SafetyRecord r) {
        SafetyRecord existing = safetyRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("记录未找到"));
        if (r.getType() != null) existing.setType(r.getType());
        if (r.getDescription() != null) existing.setDescription(r.getDescription());
        if (r.getLocation() != null) existing.setLocation(r.getLocation());
        if (r.getStatus() != null) existing.setStatus(r.getStatus());
        if (r.getResult() != null) existing.setResult(r.getResult());
        if (r.getRecordTime() != null) existing.setRecordTime(r.getRecordTime());
        return safetyRecordRepository.save(existing);
    }

    @DeleteMapping("/safety/{id}")
    public void deleteSafetyRecord(@PathVariable Long id) { safetyRecordRepository.deleteById(id); }

    // Access Records
    @GetMapping("/access")
    public List<AccessRecord> getAccessRecords() { return accessRecordRepository.findAll(); }

    @PostMapping("/access")
    public AccessRecord createAccessRecord(@RequestBody AccessRecord r) { return accessRecordRepository.save(r); }

    @PutMapping("/access/{id}")
    public AccessRecord updateAccessRecord(@PathVariable Long id, @RequestBody AccessRecord r) {
        r.setId(id);
        return accessRecordRepository.save(r);
    }

    @DeleteMapping("/access/{id}")
    public void deleteAccessRecord(@PathVariable Long id) { accessRecordRepository.deleteById(id); }
}

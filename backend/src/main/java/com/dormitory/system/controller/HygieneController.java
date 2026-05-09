package com.dormitory.system.controller;

import com.dormitory.system.entity.Hygiene;
import com.dormitory.system.repository.HygieneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hygiene")
public class HygieneController {
    @Autowired
    private HygieneRepository hygieneRepository;

    @GetMapping
    public List<Hygiene> getAll() { return hygieneRepository.findAll(); }

    @PostMapping
    public Hygiene create(@RequestBody Hygiene h) { return hygieneRepository.save(h); }

    @PutMapping("/{id}")
    public Hygiene update(@PathVariable Long id, @RequestBody Hygiene h) {
        Hygiene existing = hygieneRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        if (h.getRoom() != null) existing.setRoom(h.getRoom());
        if (h.getScore() != null) existing.setScore(h.getScore());
        if (h.getComments() != null) existing.setComments(h.getComments());
        if (h.getInspector() != null) existing.setInspector(h.getInspector());
        if (h.getCheckDate() != null) existing.setCheckDate(h.getCheckDate());
        return hygieneRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { hygieneRepository.deleteById(id); }
}

package com.dormitory.system.controller;

import com.dormitory.system.entity.Notice;
import com.dormitory.system.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    @Autowired
    private NoticeRepository noticeRepository;

    @GetMapping
    public List<Notice> getAll() { return noticeRepository.findAll(); }

    @PostMapping
    public Notice create(@RequestBody Notice n) { return noticeRepository.save(n); }

    @PutMapping("/{id}")
    public Notice update(@PathVariable Long id, @RequestBody Notice n) {
        Notice existing = noticeRepository.findById(id).orElseThrow(() -> new RuntimeException("公告未找到"));
        if (n.getTitle() != null) existing.setTitle(n.getTitle());
        if (n.getContent() != null) existing.setContent(n.getContent());
        if (n.getPublishTime() != null) existing.setPublishTime(n.getPublishTime());
        return noticeRepository.save(existing);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { noticeRepository.deleteById(id); }
}

package com.dormitory.system.controller;

import com.dormitory.system.entity.OperationLog;
import com.dormitory.system.entity.SystemParameter;
import com.dormitory.system.repository.OperationLogRepository;
import com.dormitory.system.repository.SystemParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    @Autowired
    private SystemParameterRepository parameterRepository;
    @Autowired
    private OperationLogRepository logRepository;

    // Parameters
    @GetMapping("/params")
    public List<SystemParameter> getParams() { return parameterRepository.findAll(); }

    @PostMapping("/params")
    public SystemParameter createParam(@RequestBody SystemParameter p) { return parameterRepository.save(p); }
    
    @PutMapping("/params/{id}")
    public SystemParameter updateParam(@PathVariable Long id, @RequestBody SystemParameter p) {
        p.setId(id);
        return parameterRepository.save(p);
    }

    // Logs
    @GetMapping("/logs")
    public List<OperationLog> getLogs() { return logRepository.findAll(); }
    
    @PostMapping("/logs")
    public OperationLog createLog(@RequestBody OperationLog log) { return logRepository.save(log); }
}

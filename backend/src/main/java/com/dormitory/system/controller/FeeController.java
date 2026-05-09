package com.dormitory.system.controller;

import com.dormitory.system.entity.Fee;
import com.dormitory.system.entity.FeeStandard;
import com.dormitory.system.repository.FeeRepository;
import com.dormitory.system.repository.FeeStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FeeController {
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private FeeStandardRepository feeStandardRepository;

    // Fees
    @GetMapping("/fees")
    public List<Fee> getFees() { return feeRepository.findAll(); }

    @PostMapping("/fees")
    public Fee createFee(@RequestBody Fee fee) { return feeRepository.save(fee); }

    @PutMapping("/fees/{id}")
    public Fee updateFee(@PathVariable Long id, @RequestBody Fee fee) {
        fee.setId(id);
        return feeRepository.save(fee);
    }

    @DeleteMapping("/fees/{id}")
    public void deleteFee(@PathVariable Long id) { feeRepository.deleteById(id); }

    // Standards
    @GetMapping("/fee-standards")
    public List<FeeStandard> getStandards() { return feeStandardRepository.findAll(); }

    @PostMapping("/fee-standards")
    public FeeStandard createStandard(@RequestBody FeeStandard standard) { return feeStandardRepository.save(standard); }
    
    @PutMapping("/fee-standards/{id}")
    public FeeStandard updateStandard(@PathVariable Long id, @RequestBody FeeStandard standard) {
        standard.setId(id);
        return feeStandardRepository.save(standard);
    }

    @DeleteMapping("/fee-standards/{id}")
    public void deleteStandard(@PathVariable Long id) { feeStandardRepository.deleteById(id); }
}

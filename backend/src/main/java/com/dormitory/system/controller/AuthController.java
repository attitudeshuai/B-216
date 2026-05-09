package com.dormitory.system.controller;

import com.dormitory.system.entity.User;
import com.dormitory.system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        User user = authService.login(username, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("用户名或密码错误");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Validation
            if (user.getUsername() == null || user.getPassword() == null || user.getName() == null || user.getPhone() == null) {
                return ResponseEntity.badRequest().body("缺少必填字段");
            }
            if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
                return ResponseEntity.badRequest().body("手机号格式不正确");
            }

            user.setRole("student"); // Force role to student
            User newUser = authService.register(user);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

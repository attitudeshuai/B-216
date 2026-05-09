package com.dormitory.system.config;

import com.dormitory.system.entity.*;
import com.dormitory.system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            BuildingRepository buildingRepository,
            RoomRepository roomRepository,
            NoticeRepository noticeRepository,
            DormitoryApplicationRepository applicationRepository,
            FeeStandardRepository feeStandardRepository,
            FeeRepository feeRepository,
            MaintenanceRepository maintenanceRepository,
            AccessRecordRepository accessRecordRepository,
            AttendanceRepository attendanceRepository,
            HygieneRepository hygieneRepository,
            SafetyRecordRepository safetyRecordRepository,
            AllocationRepository allocationRepository,
            SystemParameterRepository systemParameterRepository
    ) {
        return args -> {
            // 1. Users
            User admin = createUser(userRepository, "admin", "admin123", "admin", "系统管理员", null, null, "13800000001", null);
            User student1 = createUser(userRepository, "student1", "123456", "student", "张三", "2024001", "计算机一班", "13900000001", "男");
            User student2 = createUser(userRepository, "student2", "123456", "student", "李四", "2024002", "计算机一班", "13900000002", "女");
            User student3 = createUser(userRepository, "student3", "123456", "student", "王五", "2024003", "软件工程二班", "13900000003", "男");
            User student4 = createUser(userRepository, "student4", "123456", "student", "赵六", "2024004", "计算机科学一班", "13900000004", "女");
        
        // Staff
        createUser(userRepository, "staff1", "123456", "staff", "王阿姨", "ST001", null, "13800000000", "女");
            
            // 2. Buildings
            Building b1 = createBuilding(buildingRepository, "一号楼", 6, "南校区", "本科生公寓");
            Building b2 = createBuilding(buildingRepository, "二号楼", 6, "南校区", "研究生公寓");
            Building b3 = createBuilding(buildingRepository, "三号楼", 5, "北校区", "留学生公寓");

            // 3. Rooms
            Room r1_101 = createRoom(roomRepository, b1, "101", "四人间", 4, 2);
            Room r1_102 = createRoom(roomRepository, b1, "102", "四人间", 4, 4);
            Room r1_201 = createRoom(roomRepository, b1, "201", "四人间", 4, 4);
            Room r2_101 = createRoom(roomRepository, b2, "101", "双人间", 2, 1);
            Room r3_101 = createRoom(roomRepository, b3, "101", "单人间", 1, 1);

            // 4. Notices
            createNotice(noticeRepository, "欢迎新同学", "欢迎各位新同学入住本校宿舍！", admin, 2);
            createNotice(noticeRepository, "关于冬季用电安全的通知", "冬季天气干燥，请大家注意用电安全。", admin, 0);

            // 5. Fee Standards
            createFeeStandard(feeStandardRepository, "四人间住宿费", 1200.0, "YEAR", "标准四人间每学年住宿费用");
            createFeeStandard(feeStandardRepository, "双人间住宿费", 2000.0, "YEAR", "研究生双人间每学年住宿费用");
            createFeeStandard(feeStandardRepository, "宿舍网费", 30.0, "MONTH", "校园网接入费用");

            // 6. Applications
            createApplication(applicationRepository, student1, "新生入学申请", "一号楼", "四人间", "APPROVED", 15);
            createApplication(applicationRepository, student2, "希望调换到低楼层", "一号楼", "四人间", "PENDING", 1);
            createApplication(applicationRepository, student3, "申请调换宿舍", "二号楼", "双人间", "REJECTED", 3);

            // 7. Allocations
            createAllocation(allocationRepository, student1, r1_101, "1", 10);
            createAllocation(allocationRepository, student3, r1_101, "2", 10);
            createAllocation(allocationRepository, student4, r2_101, "1", 5);

            // 8. Fees
            createFee(feeRepository, student1, new BigDecimal("1200.00"), "住宿费", "PAID", 30);
            createFee(feeRepository, student2, new BigDecimal("1200.00"), "住宿费", "UNPAID", 30);
            createFee(feeRepository, student3, new BigDecimal("30.00"), "网费", "PAID", 5);

            // 9. Maintenance
            createMaintenance(maintenanceRepository, student1, "台灯损坏", "书桌台灯无法亮起", "一般", "待处理", 5);
            createMaintenance(maintenanceRepository, student2, "洗手间漏水", "洗手间水龙头关不紧", "紧急", "已完成", 48);

            // 10. Access Records
            createAccessRecord(accessRecordRepository, student1, "IN", "一号楼大门", "FACE", 2);
            createAccessRecord(accessRecordRepository, student1, "OUT", "一号楼大门", "CARD", 8);

            // 11. Attendance
            createAttendance(attendanceRepository, student2, "晚归", 1, "社团活动延迟");
            createAttendance(attendanceRepository, student1, "缺勤", 3, "未请假夜不归宿");

            // 12. Hygiene
            createHygiene(hygieneRepository, r1_101, 7, 95.0, "地面整洁", "张老师");

            // 13. Safety Records
            createSafetyRecord(safetyRecordRepository, "例行检查", "一号楼", "消防设施检查", "正常", 5);

            // 14. System Parameters
            createSystemParameter(systemParameterRepository, "ALLOW_APPLICATION", "true", "是否开启入住申请");
        };
    }

    private User createUser(UserRepository repo, String username, String password, String role, String name, String studentId, String className, String phone, String gender) {
        return repo.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setPassword(password);
            u.setRole(role);
            u.setName(name);
            u.setStudentId(studentId);
            u.setClassName(className);
            u.setPhone(phone);
            u.setGender(gender);
            return repo.save(u);
        });
    }

    private Building createBuilding(BuildingRepository repo, String name, Integer floorCount, String location, String description) {
        return repo.findAll().stream().filter(b -> name.equals(b.getName())).findFirst().orElseGet(() -> {
            Building b = new Building();
            b.setName(name);
            b.setFloorCount(floorCount);
            b.setLocation(location);
            b.setDescription(description);
            return repo.save(b);
        });
    }

    private Room createRoom(RoomRepository repo, Building building, String roomNumber, String type, Integer capacity, Integer availableBeds) {
        return repo.findAll().stream()
                .filter(r -> r.getBuilding().getId().equals(building.getId()) && r.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElseGet(() -> {
                    Room r = new Room();
                    r.setBuilding(building);
                    r.setRoomNumber(roomNumber);
                    r.setType(type);
                    r.setCapacity(capacity);
                    r.setAvailableBeds(availableBeds);
                    r.setStatus("空闲");
                    return repo.save(r);
                });
    }

    private void createNotice(NoticeRepository repo, String title, String content, User publisher, int daysAgo) {
        if (repo.findAll().stream().noneMatch(n -> n.getTitle().equals(title))) {
            Notice n = new Notice();
            n.setTitle(title);
            n.setContent(content);
            n.setPublisher(publisher);
            n.setPublishTime(LocalDateTime.now().minusDays(daysAgo));
            repo.save(n);
        }
    }

    private void createFeeStandard(FeeStandardRepository repo, String name, Double amount, String unit, String description) {
        if (repo.findAll().stream().noneMatch(f -> f.getName().equals(name))) {
            FeeStandard fs = new FeeStandard();
            fs.setName(name);
            fs.setAmount(amount);
            fs.setUnit(unit);
            fs.setDescription(description);
            repo.save(fs);
        }
    }

    private void createApplication(DormitoryApplicationRepository repo, User student, String reason, String building, String type, String status, int daysAgo) {
        if (repo.findAll().stream().noneMatch(a -> a.getStudent().getId().equals(student.getId()) && a.getReason().equals(reason))) {
            DormitoryApplication app = new DormitoryApplication();
            app.setStudent(student);
            app.setReason(reason);
            app.setPreferredBuilding(building);
            app.setPreferredRoomType(type);
            app.setStatus(status);
            app.setCreateTime(LocalDateTime.now().minusDays(daysAgo));
            repo.save(app);
        }
    }

    private void createAllocation(AllocationRepository repo, User student, Room room, String bedNumber, int daysAgo) {
        if (repo.findAll().stream().noneMatch(a -> a.getStudent().getId().equals(student.getId()))) {
            Allocation a = new Allocation();
            a.setStudent(student);
            a.setRoom(room);
            a.setBedNumber(bedNumber);
            a.setCheckInTime(LocalDateTime.now().minusDays(daysAgo));
            repo.save(a);
        }
    }

    private void createFee(FeeRepository repo, User student, BigDecimal amount, String type, String status, int deadlineDays) {
         if (repo.findAll().stream().noneMatch(f -> f.getStudent().getId().equals(student.getId()) && f.getType().equals(type) && f.getStatus().equals(status))) {
            Fee f = new Fee();
            f.setStudent(student);
            f.setAmount(amount);
            f.setType(type);
            f.setStatus(status);
            f.setDeadline(LocalDate.now().plusDays(deadlineDays));
            if ("PAID".equals(status)) {
                f.setPayDate(LocalDate.now());
            }
            repo.save(f);
        }
    }

    private void createMaintenance(MaintenanceRepository repo, User student, String title, String description, String level, String status, int hoursAgo) {
        if (repo.findAll().stream().noneMatch(m -> m.getStudent().getId().equals(student.getId()) && m.getTitle().equals(title))) {
            Maintenance m = new Maintenance();
            m.setStudent(student);
            m.setTitle(title);
            m.setDescription(description);
            m.setEmergencyLevel(level);
            m.setStatus(status);
            m.setReportTime(LocalDateTime.now().minusHours(hoursAgo));
            if ("已完成".equals(status)) {
                m.setCompleteTime(LocalDateTime.now());
                m.setResult("已修复");
            }
            repo.save(m);
        }
    }

    private void createAccessRecord(AccessRecordRepository repo, User student, String direction, String location, String method, int hoursAgo) {
        // Simple duplicate check
        LocalDateTime targetTime = LocalDateTime.now().minusHours(hoursAgo);
        boolean exists = repo.findAll().stream().anyMatch(r -> 
            r.getStudent().getId().equals(student.getId()) && 
            r.getLocation().equals(location) && 
            r.getAccessTime().getHour() == targetTime.getHour());
            
        if (!exists) {
            AccessRecord ar = new AccessRecord();
            ar.setStudent(student);
            ar.setDirection(direction);
            ar.setLocation(location);
            ar.setMethod(method);
            ar.setAccessTime(targetTime);
            repo.save(ar);
        }
    }

    private void createAttendance(AttendanceRepository repo, User student, String type, int daysAgo, String remark) {
         if (repo.findAll().stream().noneMatch(a -> a.getStudent().getId().equals(student.getId()) && a.getType().equals(type))) {
            Attendance at = new Attendance();
            at.setStudent(student);
            at.setType(type);
            at.setTime(LocalDateTime.now().minusDays(daysAgo).withHour(22));
            at.setRemark(remark);
            repo.save(at);
        }
    }

    private void createHygiene(HygieneRepository repo, Room room, int daysAgo, Double score, String comments, String inspector) {
        if (repo.findAll().stream().noneMatch(h -> h.getRoom().getId().equals(room.getId()) && h.getCheckDate().equals(LocalDate.now().minusDays(daysAgo)))) {
            Hygiene h = new Hygiene();
            h.setRoom(room);
            h.setCheckDate(LocalDate.now().minusDays(daysAgo));
            h.setScore(score);
            h.setComments(comments);
            h.setInspector(inspector);
            repo.save(h);
        }
    }

    private void createSafetyRecord(SafetyRecordRepository repo, String type, String location, String desc, String status, int daysAgo) {
         if (repo.findAll().stream().noneMatch(s -> s.getType().equals(type) && s.getLocation().equals(location) && s.getRecordTime() != null && s.getRecordTime().toLocalDate().equals(LocalDate.now().minusDays(daysAgo)))) {
            SafetyRecord sr = new SafetyRecord();
            sr.setType(type);
            sr.setLocation(location);
            sr.setDescription(desc);
            sr.setStatus(status);
            sr.setRecordTime(LocalDateTime.now().minusDays(daysAgo));
            sr.setResult("检查完成");
            repo.save(sr);
        }
    }

    private void createSystemParameter(SystemParameterRepository repo, String key, String value, String desc) {
        if (repo.findAll().stream().noneMatch(p -> key.equals(p.getParamKey()))) {
             SystemParameter sp = new SystemParameter();
             sp.setParamKey(key);
             sp.setParamValue(value);
             sp.setDescription(desc);
             repo.save(sp);
        }
    }
}

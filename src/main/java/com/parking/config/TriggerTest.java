package com.parking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class TriggerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void testTriggers() {
        // 测试违规三次自动拉黑触发器
        testBlacklistTrigger();
        
        // 测试车辆入场自动更新车位状态触发器
        testParkingSpaceStatusTrigger();
    }
    
    private void testBlacklistTrigger() {
        System.out.println("\n=== 测试违规三次自动拉黑触发器 ===");
        
        // 创建一个测试车牌号
        String testPlateNumber = "TEST-1234";
        
        try {
            // 1. 先删除可能存在的测试数据
            jdbcTemplate.update("DELETE FROM violation_record WHERE plate_number = ?", testPlateNumber);
            jdbcTemplate.update("DELETE FROM blacklist WHERE plate_number = ?", testPlateNumber);
            System.out.println("已清理测试数据");
            
            // 2. 插入第一条违规记录
            jdbcTemplate.update("INSERT INTO violation_record (plate_number, violation_type, description) VALUES (?, ?, ?)",
                    testPlateNumber, "乱停乱放", "第一次违规");
            System.out.println("插入第一条违规记录");
            
            // 检查黑名单状态
            List<Map<String, Object>> blacklist1 = jdbcTemplate.queryForList("SELECT * FROM blacklist WHERE plate_number = ?", testPlateNumber);
            System.out.println("第一次违规后黑名单状态: " + (blacklist1.isEmpty() ? "不在黑名单" : "已在黑名单"));
            
            // 3. 插入第二条违规记录
            jdbcTemplate.update("INSERT INTO violation_record (plate_number, violation_type, description) VALUES (?, ?, ?)",
                    testPlateNumber, "占用消防通道", "第二次违规");
            System.out.println("插入第二条违规记录");
            
            // 检查黑名单状态
            List<Map<String, Object>> blacklist2 = jdbcTemplate.queryForList("SELECT * FROM blacklist WHERE plate_number = ?", testPlateNumber);
            System.out.println("第二次违规后黑名单状态: " + (blacklist2.isEmpty() ? "不在黑名单" : "已在黑名单"));
            
            // 4. 插入第三条违规记录
            jdbcTemplate.update("INSERT INTO violation_record (plate_number, violation_type, description) VALUES (?, ?, ?)",
                    testPlateNumber, "恶意逃费", "第三次违规");
            System.out.println("插入第三条违规记录");
            
            // 检查黑名单状态
            List<Map<String, Object>> blacklist3 = jdbcTemplate.queryForList("SELECT * FROM blacklist WHERE plate_number = ?", testPlateNumber);
            System.out.println("第三次违规后黑名单状态: " + (blacklist3.isEmpty() ? "不在黑名单" : "已在黑名单"));
            
            if (!blacklist3.isEmpty()) {
                System.out.println("触发器工作正常！车牌号 " + testPlateNumber + " 已被自动加入黑名单");
                System.out.println("黑名单记录: " + blacklist3.get(0));
            } else {
                System.out.println("触发器工作异常！车牌号 " + testPlateNumber + " 未被加入黑名单");
            }
            
        } catch (Exception e) {
            System.err.println("测试违规三次自动拉黑触发器时发生错误: ");
            e.printStackTrace();
        }
        
        System.out.println("=== 违规三次自动拉黑触发器测试完成 ===\n");
    }
    
    private void testParkingSpaceStatusTrigger() {
        System.out.println("\n=== 测试车辆入场自动更新车位状态触发器 ===");
        
        // 创建一个测试车牌号
        String testPlateNumber = "TEST-5678";
        
        try {
            // 1. 查找一个空闲的车位(状态为0)
            List<Map<String, Object>> freeSpaces = jdbcTemplate.queryForList("SELECT * FROM parking_space WHERE status = 0 LIMIT 1");
            if (freeSpaces.isEmpty()) {
                System.out.println("没有找到空闲车位，无法测试");
                return;
            }
            
            Map<String, Object> freeSpace = freeSpaces.get(0);
            Long spaceId = (Long) freeSpace.get("ID");
            String spaceNumber = (String) freeSpace.get("SPACE_NUMBER");
            System.out.println("找到空闲车位: " + spaceNumber + " (ID: " + spaceId + ")");
            
            // 2. 插入停车记录
            jdbcTemplate.update("INSERT INTO parking_record (plate_number, space_id, entry_time) VALUES (?, ?, NOW())",
                    testPlateNumber, spaceId);
            System.out.println("插入停车记录，车牌号: " + testPlateNumber + "，车位ID: " + spaceId);
            
            // 3. 检查车位状态
            List<Map<String, Object>> updatedSpace = jdbcTemplate.queryForList("SELECT * FROM parking_space WHERE id = ?", spaceId);
            if (!updatedSpace.isEmpty()) {
                Map<String, Object> space = updatedSpace.get(0);
                int status = (int) space.get("STATUS");
                System.out.println("车位状态更新为: " + status + " (0-空闲, 1-占用, 2-已预约)");
                
                if (status == 1) {
                    System.out.println("触发器工作正常！车位 " + spaceNumber + " 已自动更新为占用状态");
                } else {
                    System.out.println("触发器工作异常！车位 " + spaceNumber + " 状态未更新为占用");
                }
            }
            
        } catch (Exception e) {
            System.err.println("测试车辆入场自动更新车位状态触发器时发生错误: ");
            e.printStackTrace();
        }
        
        System.out.println("=== 车辆入场自动更新车位状态触发器测试完成 ===\n");
    }
}
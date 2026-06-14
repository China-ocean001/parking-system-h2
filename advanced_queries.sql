-- ============================================================
-- 停车场管理系统 - 高级 SQL 查询与分析脚本
-- 用途：用于课程设计报告中的“数据库查询实现”或“系统测试”章节
-- ============================================================

-- 1. 多表连接查询 (Complex Joins)
-- 查询当前所有占用车位的详细信息（包括车位号、车牌、入场时间、车主预约信息）
SELECT 
    s.space_number AS 车位号,
    s.remark AS 车位备注,
    r.plate_number AS 当前停放车辆,
    r.entry_time AS 入场时间,
    res.user_id AS 预约用户ID,
    u.username AS 预约用户名
FROM parking_space s
JOIN parking_record r ON s.id = r.space_id
LEFT JOIN reservation res ON s.id = res.space_id AND res.status = 1 -- 关联已履约的预约记录
LEFT JOIN sys_user u ON res.user_id = u.id
WHERE s.status = 1 AND r.status = 0;

-- 2. 聚合统计查询 (Aggregation)
-- 统计每个车位的利用率（总停车次数、总收入）
SELECT 
    s.space_number,
    COUNT(r.id) AS 历史停车次数,
    COALESCE(SUM(r.fee), 0) AS 总收入
FROM parking_space s
LEFT JOIN parking_record r ON s.id = r.space_id
GROUP BY s.id, s.space_number
ORDER BY 总收入 DESC;

-- 3. 子查询 (Subqueries)
-- 查询所有“VIP宽体车位”的停车记录，且费用超过平均停车费用的记录
SELECT * 
FROM parking_record 
WHERE space_id IN (SELECT id FROM parking_space WHERE remark LIKE '%VIP%')
AND fee > (SELECT AVG(fee) FROM parking_record WHERE fee IS NOT NULL);

-- 4. 存储过程示例 (Stored Procedures)
-- 注意：在 Spring Boot + H2 中通常不直接运行此脚本，此处仅作 SQL 代码展示
/*
DELIMITER //
CREATE PROCEDURE Proc_Calculate_Daily_Report(IN reportDate DATE)
BEGIN
    -- 1. 计算当日总收入
    SELECT SUM(fee) INTO @totalIncome 
    FROM parking_record 
    WHERE DATE(exit_time) = reportDate;
    
    -- 2. 计算当日车流量
    SELECT COUNT(*) INTO @totalCars 
    FROM parking_record 
    WHERE DATE(entry_time) = reportDate;
    
    -- 3. 输出结果
    SELECT reportDate AS 日期, @totalIncome AS 总收入, @totalCars AS 总车流量;
END //
DELIMITER ;
*/

-- 5. 触发器示例 (Triggers)
-- 当车辆产生违规记录时，自动将其加入黑名单
/*
CREATE TRIGGER trg_auto_blacklist
AFTER INSERT ON violation_record
FOR EACH ROW
BEGIN
    INSERT INTO blacklist (plate_number, reason, create_time)
    VALUES (NEW.plate_number, CONCAT('自动拉黑: ', NEW.violation_type), NOW());
END;
*/

-- 6. 增强版触发器：违规三次自动拉黑
-- 当车辆违规次数达到3次时，自动将其加入黑名单
DELIMITER //
CREATE TRIGGER trg_blacklist_after_three_violations
AFTER INSERT ON violation_record
FOR EACH ROW
BEGIN
    DECLARE violation_count INT;
    
    -- 统计该车牌号的违规次数
    SELECT COUNT(*) INTO violation_count 
    FROM violation_record 
    WHERE plate_number = NEW.plate_number;
    
    -- 如果违规次数达到3次且不在黑名单中，则加入黑名单
    IF violation_count = 3 AND NOT EXISTS (SELECT 1 FROM blacklist WHERE plate_number = NEW.plate_number) THEN
        INSERT INTO blacklist (plate_number, reason, create_time)
        VALUES (NEW.plate_number, '违规次数达3次自动拉黑', NOW());
    END IF;
END //
DELIMITER ;

-- 6. 窗口函数 (Window Functions) - 分析排名
-- 按收入对每一天的停车记录进行排名
SELECT 
    plate_number,
    exit_time,
    fee,
    RANK() OVER (PARTITION BY CAST(exit_time AS DATE) ORDER BY fee DESC) AS 当日费用排名
FROM parking_record
WHERE status = 1;

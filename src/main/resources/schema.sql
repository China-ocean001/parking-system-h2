-- H2 Database Schema
-- 每次启动重置数据库，确保数据一致性

-- ==========================================
-- 1. 清理旧对象 (Drop Objects)
-- ==========================================
DROP VIEW IF EXISTS v_parking_details;
DROP VIEW IF EXISTS v_daily_income;
DROP TABLE IF EXISTS violation_record;
DROP TABLE IF EXISTS blacklist;
DROP TABLE IF EXISTS free_plate;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS parking_record;
DROP TABLE IF EXISTS parking_space;
DROP TABLE IF EXISTS sys_user;

-- ==========================================
-- 2. 表结构定义 (Table Definitions)
-- ==========================================

-- 2.1 用户表 (User)
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.2 停车位表 (ParkingSpace)
CREATE TABLE parking_space (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_number VARCHAR(20) NOT NULL,
    status INT DEFAULT 0, -- 0-空闲, 1-占用, 2-已预约
    remark VARCHAR(200)
);

-- 2.3 停车记录表 (ParkingRecord)
CREATE TABLE parking_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL,
    space_id BIGINT NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    fee DECIMAL(10, 2),
    status INT DEFAULT 0, -- 0-停车中, 1-已出场
    payment_status INT DEFAULT 0, -- 0-未支付, 1-已支付
    FOREIGN KEY (space_id) REFERENCES parking_space(id)
);

-- 2.4 免费车牌表 (FreePlate)
CREATE TABLE free_plate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- 2.5 预约记录表 (Reservation)
CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    plate_number VARCHAR(20) NOT NULL,
    reserve_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status INT DEFAULT 0, -- 0-有效, 1-已使用, 2-已取消
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (space_id) REFERENCES parking_space(id)
);

-- 2.6 黑名单表 (Blacklist)
CREATE TABLE blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    reason VARCHAR(200), -- 拉黑原因 (e.g., 欠费, 违规)
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.7 违规记录表 (ViolationRecord)
CREATE TABLE violation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL,
    violation_type VARCHAR(50) NOT NULL, -- 违规类型 (e.g., 乱停乱放, 占用消防通道)
    description VARCHAR(200),
    record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 3. 索引设计 (Indexes) - 优化查询性能
-- ==========================================
-- 加快车牌号查询 (黑名单检查, 欠费检查)
CREATE INDEX idx_record_plate ON parking_record(plate_number);
CREATE INDEX idx_blacklist_plate ON blacklist(plate_number);
CREATE INDEX idx_reservation_plate ON reservation(plate_number);

-- 加快车位状态查询 (查找空闲车位)
CREATE INDEX idx_space_status ON parking_space(status);

-- 加快未支付订单查询
CREATE INDEX idx_record_status_payment ON parking_record(status, payment_status);

-- ==========================================
-- 4. 视图设计 (Views) - 简化复杂查询
-- ==========================================

-- 4.1 实时停车详情视图
-- 关联车位信息和在场车辆记录，方便管理员查看当前停车场情况
CREATE VIEW v_parking_details AS
SELECT 
    r.id AS record_id,
    s.space_number,
    r.plate_number,
    r.entry_time,
    TIMESTAMPDIFF(MINUTE, r.entry_time, NOW()) AS duration_minutes,
    CASE 
        WHEN fp.id IS NOT NULL THEN '是' 
        ELSE '否' 
    END AS is_free_plate
FROM parking_record r
JOIN parking_space s ON r.space_id = s.id
LEFT JOIN free_plate fp ON r.plate_number = fp.plate_number
WHERE r.status = 0; -- 仅查询当前在场车辆

-- 4.2 每日收入统计视图
CREATE VIEW v_daily_income AS
SELECT 
    CAST(exit_time AS DATE) AS parking_date,
    COUNT(*) AS total_cars,
    SUM(fee) AS total_income
FROM parking_record
WHERE status = 1 AND payment_status = 1
GROUP BY CAST(exit_time AS DATE);

-- ==========================================
-- 4. 触发器定义 (Triggers)
-- ==========================================

-- 违规三次自动拉黑触发器
-- 当车辆违规次数达到3次时，自动将其加入黑名单
CREATE TRIGGER trg_blacklist_after_three_violations
AFTER INSERT ON violation_record
FOR EACH ROW
CALL "com.parking.config.BlacklistTrigger";

-- 车辆入场自动更新车位状态触发器
-- 当车辆入场时，自动将对应车位状态更新为占用(1)
CREATE TRIGGER AfterEntryInsert
AFTER INSERT ON parking_record
FOR EACH ROW
CALL "com.parking.config.ParkingSpaceStatusTrigger";

-- ==========================================
-- 5. 初始化数据 (Sample Data)
-- ==========================================

-- 5.1 用户
INSERT INTO sys_user (username, password, role) VALUES ('admin', '123456', 'ADMIN');
INSERT INTO sys_user (username, password, role) VALUES ('user', '123456', 'USER');
INSERT INTO sys_user (username, password, role) VALUES ('zhangsan', '123456', 'USER');

-- 5.2 车位 (A区: 普通, B区: VIP)
INSERT INTO parking_space (space_number, status, remark) VALUES ('A001', 0, '靠近电梯');
INSERT INTO parking_space (space_number, status, remark) VALUES ('A002', 0, '标准车位');
INSERT INTO parking_space (space_number, status, remark) VALUES ('A003', 0, '标准车位');
INSERT INTO parking_space (space_number, status, remark) VALUES ('A004', 0, '标准车位');
INSERT INTO parking_space (space_number, status, remark) VALUES ('A005', 0, '柱子旁边');
INSERT INTO parking_space (space_number, status, remark) VALUES ('B001', 0, 'VIP宽体车位');
INSERT INTO parking_space (space_number, status, remark) VALUES ('B002', 0, 'VIP宽体车位');

-- 5.3 免费车牌
INSERT INTO free_plate (plate_number, description) VALUES ('FREE-001', '内部公务车');
INSERT INTO free_plate (plate_number, description) VALUES ('FREE-002', '警务用车');

-- 5.4 黑名单
INSERT INTO blacklist (plate_number, reason) VALUES ('BLACK-001', '多次逃费');
INSERT INTO blacklist (plate_number, reason) VALUES ('BAD-888', '损坏公物未赔偿');

-- 5.5 历史停车记录 (模拟数据)
-- 已完成的订单
INSERT INTO parking_record (plate_number, space_id, entry_time, exit_time, fee, status, payment_status) 
VALUES ('京A88888', 1, DATEADD('HOUR', -5, NOW()), DATEADD('HOUR', -3, NOW()), 10.00, 1, 1);

INSERT INTO parking_record (plate_number, space_id, entry_time, exit_time, fee, status, payment_status) 
VALUES ('京C66666', 2, DATEADD('HOUR', -20, NOW()), DATEADD('HOUR', -10, NOW()), 50.00, 1, 1);

-- 正在停车的订单 (占用 B001)
INSERT INTO parking_record (plate_number, space_id, entry_time, status, payment_status) 
VALUES ('沪A11111', 6, NOW(), 0, 0);
UPDATE parking_space SET status = 1 WHERE id = 6; -- 更新车位状态为占用

-- 5.6 违规记录
INSERT INTO violation_record (plate_number, violation_type, description) 
VALUES ('BAD-888', '占用消防通道', '阻碍消防车通行');

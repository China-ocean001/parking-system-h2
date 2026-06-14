# 数据库应用系统 - 答辩指南 & 代码讲解

本文档旨在帮助您应对数据库课程设计的答辩。它涵盖了项目的核心逻辑、架构设计、数据库设计以及常见的答辩问题。

## 1. 项目概况

*   **项目名称**：停车场管理系统
*   **技术栈**：
    *   **后端**：Spring Boot 2.7.18 (Java)
    *   **ORM**：MyBatis 2.3.1 (持久层框架)
    *   **数据库**：H2 Database (In-Memory 模式，兼容 MySQL)
    *   **前端**：Thymeleaf (模板引擎) + Bootstrap 5
    *   **构建工具**：Maven
*   **核心功能**：
    *   **用户/管理员角色**：基于 Session 的权限控制。
    *   **车辆入场**：黑名单校验、欠费校验、预约匹配。
    *   **车辆出场**：自动计费（支持免费车牌逻辑）、释放车位。
    *   **车位管理**：实时状态监控（空闲/占用/预约）。
    *   **风控系统**：黑名单拦截、违规记录。

---

## 2. 数据库设计 (重点)

### 2.1 E-R 图设计逻辑
系统主要实体及其关系：
*   **用户 (User)** 1:N **预约 (Reservation)**
*   **车位 (ParkingSpace)** 1:N **预约 (Reservation)**
*   **车位 (ParkingSpace)** 1:N **停车记录 (ParkingRecord)**
*   **黑名单 (Blacklist)** 和 **违规记录 (ViolationRecord)** 是通过 `plate_number` (车牌号) 进行逻辑关联的独立实体。

### 2.2 表结构与范式
系统包含 7 张核心表，均满足 **第三范式 (3NF)**：
1.  **sys_user**：用户表。
2.  **parking_space**：车位表 (status: 0-空闲, 1-占用, 2-预约)。
3.  **parking_record**：停车记录表 (核心流水表)。
    *   *设计亮点*：直接存储 `fee` (费用)，而不是只存时长。这是为了保证历史数据的不可变性（即使未来费率调整，历史账单金额不应改变）。
4.  **reservation**：预约表。
5.  **free_plate**：免费/VIP车牌表。
6.  **blacklist**：黑名单表。
7.  **violation_record**：违规记录表。

### 2.3 数据库高级特性 (新增)
为了提升系统性能和简化查询，项目中应用了以下高级特性：
*   **索引 (Indexes)**：
    *   `idx_record_plate`：加快车牌号的查询速度（黑名单校验、欠费查询是高频操作）。
    *   `idx_space_status`：加快空闲车位 (`status=0`) 的检索速度。
*   **视图 (Views)**：
    *   `v_parking_details`：封装了多表连接查询（关联车位、记录、免费车牌），方便管理员实时查看在场车辆详情。
    *   `v_daily_income`：用于财务统计，快速计算每日停车收入。
*   **触发器 (Triggers)**：
    *   `trg_auto_blacklist`：当插入严重违规记录时，自动触发将该车辆加入黑名单。

---

## 3. 核心代码深度解析

### 3.1 车辆入场逻辑 (`ParkingService.vehicleEntry`)
这是系统最复杂的业务逻辑，包含 4 步校验：

```java
@Transactional // 开启事务，保证数据一致性
public void vehicleEntry(Long spaceId, String plateNumber) {
    // 1. 黑名单校验
    // 如果车牌在黑名单表中，直接抛出异常，拦截入场
    Blacklist bl = blacklistMapper.findByPlateNumber(plateNumber);
    if (bl != null) throw new RuntimeException("拒绝入场: 车辆已被列入黑名单...");

    // 2. 欠费校验
    // 查询该车牌是否有 status=1 (已出场) 且 payment_status=0 (未支付) 的记录
    List<ParkingRecord> unpaid = parkingRecordMapper.findUnpaidByPlateNumber(plateNumber);
    if (!unpaid.isEmpty()) throw new RuntimeException("存在未支付订单...");

    // 3. 预约匹配
    ParkingSpace space = parkingSpaceMapper.findById(spaceId);
    if (space.getStatus() == 2) { // 如果车位是“预约”状态
        // 必须是预约了该车位的同一辆车才能入场
        Reservation res = reservationMapper.findActiveBySpaceId(spaceId);
        if (!res.getPlateNumber().equals(plateNumber)) {
            throw new RuntimeException("该车位已被其他车辆预约");
        }
        // 标记预约已使用
        res.setStatus(1);
        reservationMapper.updateStatus(res);
    }

    // 4. 落库 (更新车位状态 + 插入记录)
    space.setStatus(1); // 设置为占用
    parkingSpaceMapper.updateStatus(space);
    
    ParkingRecord record = new ParkingRecord();
    // ... 设置入场时间等 ...
    parkingRecordMapper.insert(record);
}
```

### 3.2 车辆出场与计费 (`ParkingService.vehicleExit`)
```java
@Transactional
public void vehicleExit(Long spaceId) {
    // 1. 计算时长
    long durationMillis = exitTime.getTime() - record.getEntryTime().getTime();
    
    // 2. 免费车牌策略
    FreePlate freePlate = freePlateMapper.findByPlateNumber(record.getPlateNumber());
    if (freePlate != null) {
        record.setFee(BigDecimal.ZERO); // 免费
        record.setPaymentStatus(1);     // 自动标记为已支付
    } else {
        // 3. 计费公式：向上取整小时数 * 5元
        double hours = Math.ceil(durationMillis / (1000.0 * 60 * 60));
        BigDecimal fee = HOURLY_RATE.multiply(new BigDecimal(hours));
        record.setFee(fee);
    }
    
    // 4. 释放车位
    space.setStatus(0); // 变回空闲
}
```

---

## 4. 常见答辩问题 (Q&A)

**Q1: 为什么使用 H2 数据库？**
*   **A**: H2 是一个高性能的 Java 内存数据库。
    *   **开发便捷**：不需要安装额外的数据库软件，启动项目即可运行，非常适合演示和课程设计。
    *   **环境纯净**：配置为 `jdbc:h2:mem:` 模式，每次重启数据重置，保证演示时不会受脏数据影响。
    *   **兼容性**：它的 SQL 语法与 MySQL 高度兼容，代码切换到 MySQL 几乎不需要改动。

**Q2: 如何处理并发问题？（例如两个车同时抢一个车位）**
*   **A**: 目前使用了数据库事务 (`@Transactional`) 来保证操作的原子性。
    *   在实际高并发场景下，可以在 `parking_space` 表增加 `version` 字段，使用**乐观锁** (Optimistic Locking)。
    *   更新时：`UPDATE parking_space SET status=1, version=version+1 WHERE id=xx AND version=old_version`。
    *   如果更新失败（影响行数为0），说明车位已被抢占。

**Q3: 你的系统中哪里体现了触发器或存储过程？**
*   **A**: 为了展示数据库高级编程能力，我在 SQL 脚本中设计了：
    *   **存储过程** `CalculateParkingFee`：封装了计费逻辑。
    *   **触发器** `trg_auto_blacklist`：演示了当插入严重违规记录时，自动触发拉黑操作。
    *   *注：虽然实际 Java 项目常将逻辑放在 Service 层，但这些设计证明了我掌握了数据库端编程的能力。*

**Q4: 为什么出场时费用要存入数据库，而不是每次查询时动态计算？**
*   **A**: 为了数据的一致性和审计需求。
    *   费率 (`HOURLY_RATE`) 可能会随时间调整（比如明年涨价到 10元/小时）。
    *   如果只存入场时间，按新费率计算旧订单，会导致历史账单金额变动，这是财务上不允许的。
    *   因此，必须在出场结算的那一刻，将计算好的金额固化存储。

**Q5: 遇到的最大困难是什么？**
*   **A**: 
    1.  **404 路径问题**：在 IDEA 使用外部 Tomcat 部署 WAR 包时，出现了 404 错误。原因是外部 Tomcat 带有 Context Path（项目名路径），而 HTML 中的链接是硬编码的 `/login`。解决办法是使用 Thymeleaf 的 `@{/login}` 语法，自动适配路径。
    2.  **H2 文件锁**：最初使用 H2 文件模式，导致多进程访问冲突。后来改为内存模式 (`mem`)，解决了锁问题，也加快了运行速度。

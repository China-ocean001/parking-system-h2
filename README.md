# 🅿️ 停车场管理系统 (H2 版)

> Parking System H2 — 开箱即跑，零配置停车场管理系统

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.18-brightgreen?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-2.3-red?style=flat)](https://mybatis.org/)
[![H2](https://img.shields.io/badge/H2-Embedded-0044CC?style=flat)](https://www.h2database.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.0-005F0F?style=flat)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.x-7952B3?style=flat&logo=bootstrap)](https://getbootstrap.com/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=flat&logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat)](LICENSE)

</div>

---

## 📑 目录

- [📖 项目简介](#-项目简介)
- [🏗️ 系统架构](#️-系统架构)
- [✨ 功能详解](#-功能详解)
- [🛠 技术栈](#-技术栈)
- [📁 项目结构](#-项目结构)
- [🗄️ 数据库设计](#️-数据库设计)
- [🚀 快速开始](#-快速开始)
- [📝 项目文档](#-项目文档)
- [🐛 常见问题](#-常见问题)

---

## 📖 项目简介

停车场管理系统 **H2 版本** —— 内置 H2 内嵌数据库，**无需安装 MySQL**，克隆即跑，真正做到**零配置开发体验**。在 v1 基础上新增黑名单系统、预约机制、违规追踪等高级功能，附带完整的数据库触发器实现。

### 🆚 与 v1 的区别

| 特性 | v1 (parking-management-system) | v2 (parking-system-h2) |
|------|------|------|
| 数据库 | MySQL (需手动安装) | H2 内存数据库 (自动) |
| 黑名单 | ❌ | ✅ 含数据库触发器 |
| 预约系统 | ❌ | ✅ 车位提前预约 |
| 违规记录 | ❌ | ✅ 完整追踪 |
| 白名单 | ❌ | ✅ 免收费车辆 |
| 启动难度 | 需配置MySQL | 🚀 直接运行 |

---

## 🏗️ 系统架构

```
┌────────────────────────────────────────────────────────┐
│                   视图层 (Thymeleaf)                     │
│  dashboard │ blacklist │ free_plates │ reservations     │
│  users │ violations │ records │ login                  │
├────────────────────────────────────────────────────────┤
│                   控制层 (Controller)                    │
│  Admin │ Login │ Parking │ Payment │ Reservation       │
├────────────────────────────────────────────────────────┤
│                   业务层 (Service)                       │
│  ParkingService │ UserService                          │
├────────────────────────────────────────────────────────┤
│                   数据层 (MyBatis)                       │
│  7 Mappers │ 7 XML Mappings │ H2 Database              │
│                                    │                    │
│                   触发器层 (Config)  │                    │
│  BlacklistTrigger │ ParkingSpaceStatusTrigger           │
│  DatabaseTestRunner │ TriggerTest                      │
└────────────────────────────────────────────────────────┘
```

---

## ✨ 功能详解

### 🅿️ 车位管理
- 车位 CRUD 操作
- 实时状态监控（空闲/占用/预约）
- **状态自动触发器**：进出场自动更新车位占用状态

### 📋 停车记录
- 车辆入场/出场记录
- 历史记录查询
- 收入统计报表

### 🚫 黑名单系统
- 违规车辆加入黑名单
- 黑名单车辆入场自动报警
- **数据库触发器**：自动检测入场车辆是否在黑名单中

### 📅 预约系统
- 车位提前预约
- 预约时间管理
- 预约到期自动取消

### 🆓 白名单管理
- 免收费车辆登记
- VIP 车辆特权
- 特殊车辆放行

### ⚠️ 违规管理
- 违规行为记录
- 违规次数统计
- 关联黑名单自动升级

### 💰 支付管理
- 停车费用自动计算
- 支付状态追踪
- 日/月营收汇总

### 👥 用户管理
- 管理员/普通用户角色
- 登录认证
- 权限控制

---

## 🛠 技术栈

| 层级 | 技术 | 版本 | 亮点 |
|------|------|------|------|
| **框架** | Spring Boot | 2.7.18 | 稳定版 |
| **ORM** | MyBatis Spring Boot | 2.3.1 | 注解+XML混用 |
| **数据库** | H2 | 内嵌 | 零配置，即开即用 |
| **模板** | Thymeleaf | 3.0 | 自然模板 |
| **前端** | Bootstrap | 5.x | 响应式 |
| **构建** | Maven Wrapper | - | 无需安装Maven |
| **Java** | JDK | 1.8+ | 兼容性好 |

---

## 📁 项目结构

```
parking-system-h2/
│
├── pom.xml                              # Maven 依赖
├── run.bat                              # 🪟 Windows 一键启动
├── README.md                            # 项目说明
│
├── src/main/java/com/parking/
│   ├── ParkingSystemApplication.java    # 🚀 启动类
│   │
│   ├── controller/                      # 🌐 控制器 (6个)
│   │   ├── AdminController.java         # 后台管理
│   │   ├── LoginController.java         # 登录
│   │   ├── ParkingController.java       # 停车核心
│   │   ├── PaymentController.java       # 支付
│   │   └── ReservationController.java   # 预约
│   │
│   ├── entity/                          # 📦 实体类 (8个)
│   │   ├── ParkingSpace.java            # 车位
│   │   ├── ParkingRecord.java           # 停车记录
│   │   ├── Blacklist.java               # 黑名单
│   │   ├── FreePlate.java               # 白名单
│   │   ├── Reservation.java             # 预约
│   │   ├── ViolationRecord.java         # 违规记录
│   │   ├── User.java                    # 用户
│   │   └── PaymentRecord.java           # 支付(隐含)
│   │
│   ├── mapper/                          # 🗄️ MyBatis (7个)
│   │   ├── ParkingSpaceMapper.java
│   │   ├── ParkingRecordMapper.java
│   │   ├── BlacklistMapper.java
│   │   ├── FreePlateMapper.java
│   │   ├── ReservationMapper.java
│   │   ├── ViolationRecordMapper.java
│   │   └── UserMapper.java
│   │
│   ├── service/                         # 💼 业务层
│   │   ├── ParkingService.java
│   │   └── UserService.java
│   │
│   ├── config/                          # ⚙️ 高级配置
│   │   ├── BlacklistTrigger.java        # 黑名单触发器
│   │   ├── ParkingSpaceStatusTrigger.java # 车位状态触发器
│   │   ├── DatabaseTestRunner.java      # 数据库测试
│   │   └── TriggerTest.java             # 触发器测试
│   │
│   └── exception/                       # ⚠️ 异常处理
│       └── GlobalExceptionHandler.java
│
├── src/main/resources/
│   ├── application.properties           # 应用配置
│   ├── schema.sql                       # 数据库初始化
│   │
│   ├── mapper/                          # MyBatis XML (7个)
│   │   ├── ParkingSpaceMapper.xml
│   │   ├── ParkingRecordMapper.xml
│   │   ├── BlacklistMapper.xml
│   │   ├── FreePlateMapper.xml
│   │   ├── ReservationMapper.xml
│   │   ├── ViolationRecordMapper.xml
│   │   └── UserMapper.xml
│   │
│   └── templates/                       # 📄 页面 (10个)
│       ├── dashboard.html               # 仪表盘
│       ├── login.html                   # 登录页
│       ├── blacklist.html               # 黑名单
│       ├── free_plates.html             # 白名单
│       ├── reservations.html            # 预约管理
│       ├── records.html                 # 停车记录
│       ├── users.html                   # 用户管理
│       ├── violations.html              # 违规记录
│       └── error.html                   # 错误页
│
├── DEFENSE_GUIDE.md                     # 📝 答辩指南
├── FINAL_REPORT.md                      # 📊 最终报告
├── PROJECT_MAP.html                     # 🗺️ 项目地图
├── REPORT.md                            # 📄 实验报告
└── advanced_queries.sql                 # 🔍 高级查询
```

---

## 🗄️ 数据库设计

### 核心表概览

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `parking_space` | 车位表 | status (FREE/OCCUPIED/RESERVED) |
| `parking_record` | 停车记录 | entry_time, exit_time, fee |
| `blacklist` | 黑名单 | plate_number, reason, create_time |
| `free_plate` | 白名单 | plate_number, note |
| `reservation` | 预约表 | space_id, start_time, end_time |
| `violation_record` | 违规记录 | plate_number, violation_type |
| `user` | 用户表 | username, password, role |

### 触发器设计

**BlacklistTrigger**: 车辆入场时自动检测车牌是否在黑名单中，若在则拒绝入场。

**ParkingSpaceStatusTrigger**: 车辆入场/出场时自动更新对应车位的占用状态。

---

## 🚀 快速开始

### 🎯 零配置启动

```bash
# 克隆项目
git clone https://github.com/China-ocean001/parking-system-h2.git
cd parking-system-h2

# 方式一：Maven 启动
mvnw spring-boot:run

# 方式二：Windows 一键启动
run.bat
```

无需任何数据库安装！启动后访问:

| 地址 | 说明 |
|------|------|
| http://localhost:8080 | 系统首页 |
| http://localhost:8080/h2-console | H2 数据库控制台 |

### 🔑 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `123456` |
| 用户 | `user` | `123456` |

### 🗄️ H2 控制台连接信息

| 参数 | 值 |
|------|-----|
| JDBC URL | `jdbc:h2:file:./parking_db` |
| Username | `sa` |
| Password | (空) |

---

## 📝 项目文档

| 文档 | 说明 |
|------|------|
| [DEFENSE_GUIDE.md](DEFENSE_GUIDE.md) | 毕业答辩准备指南 |
| [FINAL_REPORT.md](FINAL_REPORT.md) | 项目最终报告 |
| [FINAL_REPORT_VIEWER.html](FINAL_REPORT_VIEWER.html) | 报告可视化 |
| [PROJECT_FULL_GUIDE.html](PROJECT_FULL_GUIDE.html) | 项目完整指南 |
| [REPORT.md](REPORT.md) | 实验报告 |
| [PROJECT_MAP.html](PROJECT_MAP.html) | 项目结构地图 |

---

## 🐛 常见问题

<details>
<summary><b>Q: 端口被占用</b></summary>

修改 `application.properties` 中 `server.port=8080` 为其他端口。
</details>

<details>
<summary><b>Q: H2 数据库数据丢失</b></summary>

H2 使用文件模式 (`jdbc:h2:file:./parking_db`)，数据保存在项目目录下，重启不会丢失。
</details>

<details>
<summary><b>Q: 如何切换到 MySQL</b></summary>

修改 `application.properties` 中的数据源配置，并添加 MySQL 驱动依赖即可。
</details>

---

## 📄 License

MIT License — 仅供学习交流使用

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star！**

</div>

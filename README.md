# 🅿️ 停车场管理系统 (H2 版)

> Parking System H2 — 开箱即跑的 Spring Boot 停车场管理系统

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-2.3-red.svg)](https://mybatis.org/)
[![H2](https://img.shields.io/badge/H2-Embedded-0044CC.svg)](https://www.h2database.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.0-005F0F.svg)](https://www.thymeleaf.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📖 项目简介

停车场管理系统 H2 版本，内置 H2 内存数据库，无需安装 MySQL，真正做到**开箱即跑**。包含黑名单管理、预约系统、违规记录等高级功能。

## ✨ 功能特性

- 🅿️ **车位管理**：实时状态监控，自动触发器更新占用状态
- 📋 **停车记录**：完整入场/出场记录追踪
- 🚫 **黑名单系统**：违规车辆黑名单管理，数据库触发器自动检测
- 📅 **预约系统**：车位提前预约
- 🆓 **白名单**：免收费车辆管理
- ⚠️ **违规记录**：停车违规行为追踪
- 💰 **支付管理**：停车费用结算
- 👥 **用户管理**：管理员/普通用户

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| **框架** | Spring Boot 2.7.18 |
| **ORM** | MyBatis + MyBatis Spring Boot 2.3.1 |
| **数据库** | H2 内嵌数据库（零配置） |
| **模板引擎** | Thymeleaf |
| **构建工具** | Maven |
| **前端** | Bootstrap + 纯 HTML |

## 📁 项目结构

```
parking-system-h2/
├── src/main/java/com/parking/
│   ├── controller/             # 控制器层
│   │   ├── AdminController.java
│   │   ├── LoginController.java
│   │   ├── ParkingController.java
│   │   ├── PaymentController.java
│   │   └── ReservationController.java
│   ├── entity/                 # 实体类
│   │   ├── ParkingSpace.java
│   │   ├── ParkingRecord.java
│   │   ├── Blacklist.java
│   │   ├── FreePlate.java
│   │   ├── Reservation.java
│   │   ├── ViolationRecord.java
│   │   └── User.java
│   ├── mapper/                 # MyBatis Mapper
│   ├── service/                # 业务逻辑层
│   └── config/                 # 配置（含触发器）
│       ├── BlacklistTrigger.java
│       ├── ParkingSpaceStatusTrigger.java
│       └── DatabaseTestRunner.java
├── src/main/resources/
│   ├── schema.sql              # 数据库表结构
│   └── templates/              # 页面模板（11个页面）
├── DEFENSE_GUIDE.md            # 答辩指南
├── FINAL_REPORT.md             # 最终报告
└── pom.xml
```

## 🚀 快速开始

### 零配置启动

```bash
# 无需安装任何数据库！
mvn spring-boot:run
```

H2 控制台：http://localhost:8080/h2-console

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `123456` |
| 用户 | `user` | `123456` |

## 📝 文档

- [答辩指南](DEFENSE_GUIDE.md)
- [最终报告](FINAL_REPORT.md)
- [项目完整指南](PROJECT_FULL_GUIDE.html)

## 📄 License

MIT License — 仅供学习交流使用

# 停车场管理系统 (Parking System)

这是一个基于 Spring Boot + MyBatis + Thymeleaf + MySQL 的简单停车场管理系统，适合作为数据库课程设计或 Java Web 入门项目。

## 功能特性

1.  **用户管理**: 简单的登录功能（管理员/普通用户）。
2.  **车位管理**: 可视化展示车位状态（空闲/占用），管理员可添加/删除车位。
3.  **车辆入场**: 选择空闲车位进行车辆登记。
4.  **车辆出场**: 自动计算停车时长和费用（默认 5元/小时）。
5.  **历史记录**: 查看所有停车历史记录。

## 技术栈

*   **后端**: Java 1.8+, Spring Boot 2.7.x, MyBatis
*   **前端**: Thymeleaf, Bootstrap 5
*   **数据库**: MySQL 5.7+ / 8.0

## 快速开始

### 1. 数据库配置

1.  确保本地已安装 MySQL 数据库。
2.  使用数据库管理工具（如 Navicat, MySQL Workbench）连接数据库。
3.  执行 `src/main/resources/sql/schema.sql` 脚本，这将创建 `parking_db` 数据库、表结构并初始化数据。
4.  修改 `src/main/resources/application.properties` 中的数据库用户名和密码：
    ```properties
    spring.datasource.username=root
    spring.datasource.password=你的密码
    ```

### 2. 运行项目

1.  使用 IDEA 打开本项目。
2.  等待 Maven 依赖下载完成。
3.  运行 `src/main/java/com/parking/ParkingSystemApplication.java` 中的 `main` 方法。

### 3. 访问系统

*   浏览器打开: `http://localhost:8080`
*   **管理员账号**: `admin` / `123456`
*   **普通用户**: `user` / `123456`

## 目录结构

*   `src/main/java`: 后端源码
    *   `controller`: 控制层 (Web接口)
    *   `service`: 业务逻辑层
    *   `mapper`: DAO层接口
    *   `entity`: 实体类
*   `src/main/resources`: 资源文件
    *   `mapper`: MyBatis XML 映射文件
    *   `templates`: Thymeleaf 前端页面
    *   `sql`: 数据库脚本
    *   `application.properties`: 配置文件

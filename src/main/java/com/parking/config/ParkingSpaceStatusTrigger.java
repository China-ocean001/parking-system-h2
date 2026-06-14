package com.parking.config;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParkingSpaceStatusTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        // 初始化触发器，可以在这里做一些准备工作
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // newRow 包含插入的新记录的所有字段
        Long spaceId = (Long) newRow[2]; // parking_record中的space_id字段(索引从0开始)
        
        // 更新车位状态为占用(1)
        String updateSql = "UPDATE parking_space SET status = 1 WHERE id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setLong(1, spaceId);
            updateStmt.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        // 关闭触发器，可以在这里释放资源
    }

    @Override
    public void remove() throws SQLException {
        // 移除触发器时调用
    }
}
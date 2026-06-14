package com.parking.config;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BlacklistTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        // 初始化触发器，可以在这里做一些准备工作
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // newRow 包含插入的新记录的所有字段
        String plateNumber = (String) newRow[1]; // 违规记录中的车牌号字段(索引从0开始)
        
        // 统计该车牌号的违规次数
        String countSql = "SELECT COUNT(*) FROM violation_record WHERE plate_number = ?";
        try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
            countStmt.setString(1, plateNumber);
            try (ResultSet rs = countStmt.executeQuery()) {
                if (rs.next()) {
                    int violationCount = rs.getInt(1);
                    
                    // 如果违规次数达到3次
                    if (violationCount == 3) {
                        // 检查是否已经在黑名单中
                        String checkSql = "SELECT 1 FROM blacklist WHERE plate_number = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setString(1, plateNumber);
                            try (ResultSet checkRs = checkStmt.executeQuery()) {
                                // 如果不在黑名单中，则加入黑名单
                                if (!checkRs.next()) {
                                    String insertSql = "INSERT INTO blacklist (plate_number, reason, create_time) VALUES (?, ?, ?)";
                                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                        insertStmt.setString(1, plateNumber);
                                        insertStmt.setString(2, "违规次数达3次自动拉黑");
                                        insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                                        insertStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
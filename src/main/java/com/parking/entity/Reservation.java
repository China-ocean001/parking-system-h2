package com.parking.entity;

import java.io.Serializable;
import java.util.Date;

public class Reservation implements Serializable {
    private Long id;
    private Long userId;
    private Long spaceId;
    private String plateNumber;
    private Date reserveTime;
    private Integer status; // 0-Active, 1-Used, 2-Cancelled

    // Transient fields
    private String spaceNumber;
    private String username;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSpaceId() { return spaceId; }
    public void setSpaceId(Long spaceId) { this.spaceId = spaceId; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public Date getReserveTime() { return reserveTime; }
    public void setReserveTime(Date reserveTime) { this.reserveTime = reserveTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getSpaceNumber() { return spaceNumber; }
    public void setSpaceNumber(String spaceNumber) { this.spaceNumber = spaceNumber; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

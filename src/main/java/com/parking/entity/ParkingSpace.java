package com.parking.entity;

import java.io.Serializable;

public class ParkingSpace implements Serializable {
    private Long id;
    private String spaceNumber;
    private Integer status; // 0-Available, 1-Occupied
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSpaceNumber() { return spaceNumber; }
    public void setSpaceNumber(String spaceNumber) { this.spaceNumber = spaceNumber; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}

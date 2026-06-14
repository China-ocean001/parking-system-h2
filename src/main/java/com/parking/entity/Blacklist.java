package com.parking.entity;

import java.io.Serializable;
import java.util.Date;

public class Blacklist implements Serializable {
    private Long id;
    private String plateNumber;
    private String reason;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}

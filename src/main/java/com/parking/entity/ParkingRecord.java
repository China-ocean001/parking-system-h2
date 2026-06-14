package com.parking.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ParkingRecord implements Serializable {
    private Long id;
    private String plateNumber;
    private Long spaceId;
    private Date entryTime;
    private Date exitTime;
    private BigDecimal fee;
    private Integer status; // 0-Active, 1-Completed
    private Integer paymentStatus; // 0-Unpaid, 1-Paid

    // Transient field for display
    private String spaceNumber;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public Long getSpaceId() { return spaceId; }
    public void setSpaceId(Long spaceId) { this.spaceId = spaceId; }
    public Date getEntryTime() { return entryTime; }
    public void setEntryTime(Date entryTime) { this.entryTime = entryTime; }
    public Date getExitTime() { return exitTime; }
    public void setExitTime(Date exitTime) { this.exitTime = exitTime; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Integer paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getSpaceNumber() { return spaceNumber; }
    public void setSpaceNumber(String spaceNumber) { this.spaceNumber = spaceNumber; }
}

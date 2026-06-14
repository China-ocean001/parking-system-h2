package com.parking.entity;

import java.io.Serializable;
import java.util.Date;

public class ViolationRecord implements Serializable {
    private Long id;
    private String plateNumber;
    private String violationType;
    private String description;
    private Date recordTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getRecordTime() { return recordTime; }
    public void setRecordTime(Date recordTime) { this.recordTime = recordTime; }
}

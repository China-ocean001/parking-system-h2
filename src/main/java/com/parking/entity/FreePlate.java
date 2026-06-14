package com.parking.entity;

import java.io.Serializable;

public class FreePlate implements Serializable {
    private Long id;
    private String plateNumber;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

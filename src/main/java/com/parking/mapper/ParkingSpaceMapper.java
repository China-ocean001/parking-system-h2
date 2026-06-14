package com.parking.mapper;

import com.parking.entity.ParkingSpace;
import java.util.List;

public interface ParkingSpaceMapper {
    List<ParkingSpace> findAll();
    ParkingSpace findById(Long id);
    int insert(ParkingSpace space);
    int updateStatus(ParkingSpace space);
    int deleteById(Long id);
}

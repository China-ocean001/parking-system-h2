package com.parking.mapper;

import com.parking.entity.ParkingRecord;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ParkingRecordMapper {
    List<ParkingRecord> findAll();
    ParkingRecord findActiveBySpaceId(Long spaceId);
    ParkingRecord findById(Long id);
    List<ParkingRecord> findUnpaidByPlateNumber(@Param("plateNumber") String plateNumber);
    int insert(ParkingRecord record);
    int update(ParkingRecord record);
}

package com.parking.mapper;

import com.parking.entity.ViolationRecord;
import java.util.List;

public interface ViolationRecordMapper {
    int insert(ViolationRecord record);
    List<ViolationRecord> findAll();
}

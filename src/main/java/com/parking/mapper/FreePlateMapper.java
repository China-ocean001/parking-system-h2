package com.parking.mapper;

import com.parking.entity.FreePlate;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface FreePlateMapper {
    int insert(FreePlate freePlate);
    int deleteById(Long id);
    List<FreePlate> findAll();
    FreePlate findByPlateNumber(@Param("plateNumber") String plateNumber);
}

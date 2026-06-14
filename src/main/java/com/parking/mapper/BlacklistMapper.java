package com.parking.mapper;

import com.parking.entity.Blacklist;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BlacklistMapper {
    int insert(Blacklist blacklist);
    int deleteById(Long id);
    List<Blacklist> findAll();
    Blacklist findByPlateNumber(@Param("plateNumber") String plateNumber);
}

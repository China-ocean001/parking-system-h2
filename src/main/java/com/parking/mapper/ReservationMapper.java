package com.parking.mapper;

import com.parking.entity.Reservation;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ReservationMapper {
    int insert(Reservation reservation);
    int updateStatus(Reservation reservation);
    List<Reservation> findByUserId(Long userId);
    Reservation findActiveBySpaceId(@Param("spaceId") Long spaceId);
    Reservation findById(Long id);
}

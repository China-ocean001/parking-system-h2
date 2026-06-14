package com.parking.service;

import com.parking.entity.Blacklist;
import com.parking.entity.FreePlate;
import com.parking.entity.ParkingRecord;
import com.parking.entity.ParkingSpace;
import com.parking.entity.Reservation;
import com.parking.entity.ViolationRecord;
import com.parking.mapper.BlacklistMapper;
import com.parking.mapper.FreePlateMapper;
import com.parking.mapper.ParkingRecordMapper;
import com.parking.mapper.ParkingSpaceMapper;
import com.parking.mapper.ReservationMapper;
import com.parking.mapper.ViolationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ParkingService {

    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Autowired
    private ParkingRecordMapper parkingRecordMapper;

    @Autowired
    private FreePlateMapper freePlateMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private BlacklistMapper blacklistMapper;

    @Autowired
    private ViolationRecordMapper violationRecordMapper;

    private static final BigDecimal HOURLY_RATE = new BigDecimal("5.00"); // 5 yuan per hour

    public List<ParkingSpace> getAllSpaces() {
        return parkingSpaceMapper.findAll();
    }

    public List<ParkingRecord> getAllRecords() {
        return parkingRecordMapper.findAll();
    }

    public void addSpace(String spaceNumber, String remark) {
        ParkingSpace space = new ParkingSpace();
        space.setSpaceNumber(spaceNumber);
        space.setStatus(0);
        space.setRemark(remark);
        parkingSpaceMapper.insert(space);
    }

    public void deleteSpace(Long id) {
        parkingSpaceMapper.deleteById(id);
    }

    // Free Plate Methods
    public List<FreePlate> getAllFreePlates() {
        return freePlateMapper.findAll();
    }

    public void addFreePlate(String plateNumber, String description) {
        FreePlate fp = new FreePlate();
        fp.setPlateNumber(plateNumber);
        fp.setDescription(description);
        freePlateMapper.insert(fp);
    }

    public void deleteFreePlate(Long id) {
        freePlateMapper.deleteById(id);
    }

    // Blacklist Methods
    public List<Blacklist> getAllBlacklists() {
        return blacklistMapper.findAll();
    }

    public void addBlacklist(String plateNumber, String reason) {
        Blacklist bl = new Blacklist();
        bl.setPlateNumber(plateNumber);
        bl.setReason(reason);
        blacklistMapper.insert(bl);
    }

    public void deleteBlacklist(Long id) {
        blacklistMapper.deleteById(id);
    }

    // Violation Methods
    public List<ViolationRecord> getAllViolations() {
        return violationRecordMapper.findAll();
    }

    @Transactional
    public void addViolation(String plateNumber, String type, String description) {
        ViolationRecord vr = new ViolationRecord();
        vr.setPlateNumber(plateNumber);
        vr.setViolationType(type);
        vr.setDescription(description);
        violationRecordMapper.insert(vr);

        // Optionally auto-add to blacklist? Let's keep it manual for now or auto if needed.
        // But the user request said "blacklist vehicles (unpaid / violation) not allowed".
        // So adding to blacklist is a separate admin action usually, or we can auto-add here.
        // Let's implement auto-add to blacklist for severe violations if requested, but manual is safer.
        // For now, just recording violation.
    }

    // Reservation Methods
    public List<Reservation> getUserReservations(Long userId) {
        return reservationMapper.findByUserId(userId);
    }

    @Transactional
    public void reserveSpace(Long userId, Long spaceId, String plateNumber) {
        // Check Blacklist
        Blacklist bl = blacklistMapper.findByPlateNumber(plateNumber);
        if (bl != null) {
            throw new RuntimeException("车辆已被列入黑名单: " + bl.getReason());
        }

        // Check for unpaid records
        List<ParkingRecord> unpaid = parkingRecordMapper.findUnpaidByPlateNumber(plateNumber);
        if (!unpaid.isEmpty()) {
            throw new RuntimeException("车辆 " + plateNumber + " 存在未支付订单，请先完成支付。");
        }

        ParkingSpace space = parkingSpaceMapper.findById(spaceId);
        if (space == null || space.getStatus() != 0) {
            throw new RuntimeException("车位不可用");
        }
        
        // Update space to Reserved (2)
        space.setStatus(2);
        parkingSpaceMapper.updateStatus(space);

        // Create Reservation
        Reservation res = new Reservation();
        res.setUserId(userId);
        res.setSpaceId(spaceId);
        res.setPlateNumber(plateNumber);
        res.setStatus(0); // Active
        reservationMapper.insert(res);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation res = reservationMapper.findById(reservationId);
        if (res != null && res.getStatus() == 0) {
            // Update reservation status
            res.setStatus(2); // Cancelled
            reservationMapper.updateStatus(res);
            
            // Update space status
            ParkingSpace space = new ParkingSpace();
            space.setId(res.getSpaceId());
            space.setStatus(0); // Free
            parkingSpaceMapper.updateStatus(space);
        }
    }
    
    // Improved vehicleEntry to handle reservations
    @Transactional
    public void vehicleEntry(Long spaceId, String plateNumber) {
        // Check Blacklist
        Blacklist bl = blacklistMapper.findByPlateNumber(plateNumber);
        if (bl != null) {
            throw new RuntimeException("拒绝入场: 车辆已被列入黑名单 (" + bl.getReason() + ")");
        }

        // Check for unpaid records
        List<ParkingRecord> unpaid = parkingRecordMapper.findUnpaidByPlateNumber(plateNumber);
        if (!unpaid.isEmpty()) {
            throw new RuntimeException("车辆 " + plateNumber + " 存在未支付订单，请先完成支付。");
        }

        ParkingSpace space = parkingSpaceMapper.findById(spaceId);
        if (space == null) throw new RuntimeException("未找到车位");

        if (space.getStatus() == 1) {
            throw new RuntimeException("车位已占用");
        }

        if (space.getStatus() == 2) {
            // Check reservation
            Reservation res = reservationMapper.findActiveBySpaceId(spaceId);
            if (res == null || !res.getPlateNumber().equals(plateNumber)) {
                throw new RuntimeException("该车位已被其他车辆预约");
            }
            // Mark reservation as used
            res.setStatus(1);
            reservationMapper.updateStatus(res);
        }

        // Update space status
        space.setStatus(1);
        parkingSpaceMapper.updateStatus(space);

        // Create record
        ParkingRecord record = new ParkingRecord();
        record.setSpaceId(spaceId);
        record.setPlateNumber(plateNumber);
        record.setEntryTime(new Date());
        record.setStatus(0);
        record.setPaymentStatus(0); // Unpaid
        parkingRecordMapper.insert(record);
    }

    @Transactional
    public void vehicleExit(Long spaceId) {
        // 1. Find active record
        ParkingRecord record = parkingRecordMapper.findActiveBySpaceId(spaceId);
        if (record == null) {
            throw new RuntimeException("该车位无车辆");
        }

        // 2. Update record (calculate fee)
        Date exitTime = new Date();
        record.setExitTime(exitTime);
        
        // Check if Free Plate
        FreePlate freePlate = freePlateMapper.findByPlateNumber(record.getPlateNumber());
        if (freePlate != null) {
            record.setFee(BigDecimal.ZERO);
            record.setPaymentStatus(1); // Auto-paid (Free)
        } else {
            long durationMillis = exitTime.getTime() - record.getEntryTime().getTime();
            double hours = Math.ceil(durationMillis / (1000.0 * 60 * 60)); // Round up
            if (hours < 1) hours = 1;
            BigDecimal fee = HOURLY_RATE.multiply(new BigDecimal(hours));
            record.setFee(fee);
            record.setPaymentStatus(0);
        }
        
        record.setStatus(1); // Completed
        parkingRecordMapper.update(record);

        // 3. Update space status
        ParkingSpace space = new ParkingSpace();
        space.setId(spaceId);
        space.setStatus(0);
        parkingSpaceMapper.updateStatus(space);
    }

    @Transactional
    public void payFee(Long recordId) {
        ParkingRecord record = parkingRecordMapper.findById(recordId);
        if (record != null && record.getStatus() == 1) {
            record.setPaymentStatus(1); // Paid
            parkingRecordMapper.update(record);
        }
    }
}

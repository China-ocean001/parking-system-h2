package com.parking.config;

import com.parking.mapper.ParkingSpaceMapper;
import com.parking.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTestRunner implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> Testing Database Connection and Mappers...");
        
        try {
            System.out.println("1. Finding Admin User...");
            System.out.println("Admin User: " + userMapper.findByUsername("admin"));
            
            System.out.println("2. Finding All Spaces...");
            System.out.println("Spaces count: " + parkingSpaceMapper.findAll().size());
            
            System.out.println(">>> Database Test Passed!");
        } catch (Exception e) {
            System.err.println(">>> Database Test FAILED!");
            e.printStackTrace();
        }
    }
}

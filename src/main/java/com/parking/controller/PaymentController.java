package com.parking.controller;

import com.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class PaymentController {

    @Autowired
    private ParkingService parkingService;

    private boolean checkLogin(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @PostMapping("/payment/pay")
    public String payFee(@RequestParam Long recordId, HttpSession session) {
        if (!checkLogin(session)) return "redirect:/login";
        parkingService.payFee(recordId);
        return "redirect:/records";
    }
}

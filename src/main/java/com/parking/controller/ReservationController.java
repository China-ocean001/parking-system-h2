package com.parking.controller;

import com.parking.entity.User;
import com.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservationController {

    @Autowired
    private ParkingService parkingService;

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/reservations")
    public String myReservations(Model model, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("reservations", parkingService.getUserReservations(user.getId()));
        return "reservations";
    }

    @PostMapping("/reserve")
    public String reserve(@RequestParam Long spaceId, @RequestParam String plateNumber, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        try {
            parkingService.reserveSpace(user.getId(), spaceId, plateNumber);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/reservations/cancel")
    public String cancel(@RequestParam Long reservationId, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        try {
            parkingService.cancelReservation(reservationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/reservations";
    }
}

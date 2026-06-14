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
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    private boolean checkLogin(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!checkLogin(session)) return "redirect:/login";
        model.addAttribute("spaces", parkingService.getAllSpaces());
        model.addAttribute("user", session.getAttribute("user"));
        // 确保 dashboard.html 模板文件存在于 src/main/resources/templates/ 目录下
        return "dashboard";
    }

    @GetMapping("/records")
    public String records(Model model, HttpSession session) {
        if (!checkLogin(session)) return "redirect:/login";
        model.addAttribute("records", parkingService.getAllRecords());
        return "records";
    }

    @PostMapping("/space/add")
    public String addSpace(@RequestParam String spaceNumber, @RequestParam String remark, HttpSession session) {
        if (!checkLogin(session)) return "redirect:/login";
        parkingService.addSpace(spaceNumber, remark);
        return "redirect:/dashboard";
    }

    @GetMapping("/space/delete")
    public String deleteSpace(@RequestParam Long id, HttpSession session) {
        if (!checkLogin(session)) return "redirect:/login";
        try {
            parkingService.deleteSpace(id);
        } catch (Exception e) {
            // Handle error (e.g., cannot delete if occupied)
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/vehicle/entry")
    public String entry(@RequestParam Long spaceId, @RequestParam String plateNumber, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkLogin(session)) return "redirect:/login";
        try {
            parkingService.vehicleEntry(spaceId, plateNumber);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/vehicle/exit")
    public String exit(@RequestParam Long spaceId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkLogin(session)) return "redirect:/login";
        try {
            parkingService.vehicleExit(spaceId);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}

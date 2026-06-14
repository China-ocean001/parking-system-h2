package com.parking.controller;

import com.parking.entity.User;
import com.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    private boolean checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }

    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username, @RequestParam String password, @RequestParam String role, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        try {
            userService.addUser(username, password, role);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete")
    public String deleteUser(@RequestParam Long id, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // Free Plate Management
    @Autowired
    private com.parking.service.ParkingService parkingService; // Inject for free plates

    @GetMapping("/free-plates")
    public String listFreePlates(Model model, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        model.addAttribute("freePlates", parkingService.getAllFreePlates());
        return "free_plates";
    }

    @PostMapping("/free-plates/add")
    public String addFreePlate(@RequestParam String plateNumber, @RequestParam String description, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        try {
            parkingService.addFreePlate(plateNumber, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/free-plates";
    }

    // Blacklist Management
    @GetMapping("/blacklist")
    public String listBlacklist(Model model, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        model.addAttribute("blacklist", parkingService.getAllBlacklists());
        return "blacklist";
    }

    @PostMapping("/blacklist/add")
    public String addBlacklist(@RequestParam String plateNumber, @RequestParam String reason, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        try {
            parkingService.addBlacklist(plateNumber, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/blacklist";
    }

    @GetMapping("/blacklist/delete")
    public String deleteBlacklist(@RequestParam Long id, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        parkingService.deleteBlacklist(id);
        return "redirect:/admin/blacklist";
    }

    // Violation Management
    @GetMapping("/violations")
    public String listViolations(Model model, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        model.addAttribute("violations", parkingService.getAllViolations());
        return "violations";
    }

    @PostMapping("/violations/add")
    public String addViolation(@RequestParam String plateNumber, @RequestParam String type, @RequestParam String description, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/dashboard";
        try {
            parkingService.addViolation(plateNumber, type, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/violations";
    }
}

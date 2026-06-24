package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = {"/", "/index", "/home"})
    public String home() { return "index"; }

    @GetMapping("/login")
    public String adminLogin() { return "login"; }

    @GetMapping("/register")
    public String adminRegister() { return "register"; }

    @GetMapping("/dashboard")
    public String adminDashboard() { return "dashboard"; }

    @GetMapping("/admin-dashboard")
    public String adminDashboardAlternate() { return "dashboard"; }

    @GetMapping("/maintenance")
    public String adminMaintenance() { return "maintenance"; }

    @GetMapping("/materials")
    public String adminMaterials() { return "materials"; }

    @GetMapping("/spareparts")
    public String adminSpareparts() { return "spareparts"; }

    @GetMapping("/suppliers")
    public String adminSuppliers() { return "suppliers"; }

    @GetMapping("/staffmanagement")
    public String adminStaffManagement() { return "staffmanagement"; }

    @GetMapping("/energy")
    public String adminEnergy() { return "energy"; }

    @GetMapping("/technicians")
    public String adminTechnicians() { return "technicians"; }

    @GetMapping("/admin-attendance")
    public String adminAttendance() { return "admin-attendance"; }

    @GetMapping("/manager-login")
    public String managerLogin() { return "manager-login"; }

    @GetMapping("/manager-register")
    public String managerRegister() { return "manager-register"; }

    @GetMapping("/manager-dashboard")
    public String managerDashboard() { return "manager-dashboard"; }

    @GetMapping("/manager-maintenance")
    public String managerMaintenance() { return "manager-maintenance"; }

    @GetMapping("/manager-materials")
    public String managerMaterials() { return "manager-materials"; }

    @GetMapping("/manager-spareparts")
    public String managerSpareparts() { return "manager-spareparts"; }

    @GetMapping("/manager-suppliers")
    public String managerSuppliers() { return "manager-suppliers"; }

    @GetMapping("/manager-staffmanagement")
    public String managerStaffManagement() { return "manager-staffmanagement"; }

    @GetMapping("/manager-energy")
    public String managerEnergy() { return "manager-energy"; }

    @GetMapping("/manager-attendance")
    public String managerAttendance() { return "manager-attendance"; }

    @GetMapping("/tech-login")
    public String techLogin() { return "tech-login"; }

    @GetMapping("/tech-register")
    public String techRegister() { return "tech-register"; }

    @GetMapping("/tech-dashboard")
    public String techDashboard() { return "tech-dashboard"; }

    @GetMapping("/tech-energy")
    public String techEnergy() { return "tech-energy"; }

    @GetMapping("/tech-spareparts")
    public String techSpareparts() { return "tech-spareparts"; }

    @GetMapping("/attendance-technician")
    public String techAttendance() { return "attendance-technician"; }

    @GetMapping("/staff-login")
    public String staffLogin() { return "staff-login"; }

    @GetMapping("/staff-register")
    public String staffRegister() { return "staff-register"; }

    @GetMapping("/staff-dashboard")
    public String staffDashboard() { return "staff-dashboard"; }

    @GetMapping("/staff-materials")
    public String staffMaterials() { return "staff-materials"; }

    @GetMapping("/staff-spareparts")
    public String staffSpareparts() { return "staff-spareparts"; }

    @GetMapping("/staff-maintenance")
    public String staffMaintenance() { return "staff-maintenance"; }

    @GetMapping("/staff-energy")
    public String staffEnergy() { return "staff-energy"; }

    @GetMapping("/attendance-staff")
    public String staffAttendance() { return "attendance-staff"; }

    @GetMapping("/ai-chat")
    public String aiChat() {
        return "ai-chat";
    }

    @GetMapping("/password-reset")
    public String passwordReset() { return "password-reset"; }

}
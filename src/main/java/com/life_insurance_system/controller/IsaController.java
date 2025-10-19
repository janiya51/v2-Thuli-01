package com.life_insurance_system.controller;

import com.life_insurance_system.model.SystemAnnouncement;
import com.life_insurance_system.model.User;
import com.life_insurance_system.service.AuditLogService;
import com.life_insurance_system.service.SystemAnnouncementService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/isa")
public class IsaController {

    @Autowired
    private SystemAnnouncementService systemAnnouncementService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/dashboard")
    public String isaDashboard() {
        return "isa/dashboard";
    }

    @GetMapping("/announcements")
    public String manageAnnouncements(Model model) {
        model.addAttribute("announcements", systemAnnouncementService.getAllAnnouncements());
        model.addAttribute("announcement", new SystemAnnouncement());
        return "isa/manage_announcements";
    }

    @PostMapping("/announcements/create")
    public String createAnnouncement(@ModelAttribute SystemAnnouncement announcement, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        announcement.setIsa(user);
        systemAnnouncementService.createAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("success", "Announcement created successfully!");
        return "redirect:/isa/announcements";
    }

    @GetMapping("/announcements/edit/{id}")
    public String editAnnouncementPage(@PathVariable int id, Model model) {
        model.addAttribute("announcement", systemAnnouncementService.getAnnouncementById(id));
        return "isa/edit_announcement";
    }

    @PostMapping("/announcements/edit")
    public String editAnnouncement(@ModelAttribute SystemAnnouncement announcement, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        announcement.setIsa(user);
        systemAnnouncementService.updateAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("success", "Announcement updated successfully!");
        return "redirect:/isa/announcements";
    }

    @GetMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable int id, RedirectAttributes redirectAttributes) {
        systemAnnouncementService.deleteAnnouncement(id);
        redirectAttributes.addFlashAttribute("success", "Announcement deleted successfully!");
        return "redirect:/isa/announcements";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        String roleName = user.getRole().getRoleName();
        String dashboardUrl = switch (roleName) {
            case "IT System Analyst" -> "/isa/dashboard";
            case "HR/Admin Manager" -> "/admin/dashboard";
            default -> "/login";
        };
        model.addAttribute("dashboardUrl", dashboardUrl);
        model.addAttribute("auditLogs", auditLogService.getAllAuditLogs());
        return "isa/view_audit_logs";
    }
}
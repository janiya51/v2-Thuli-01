package com.life_insurance_system.controller;

import com.life_insurance_system.model.User;
import com.life_insurance_system.service.UserService;
import com.life_insurance_system.model.PolicyDispute;
import com.life_insurance_system.service.PolicyDisputeService;
import com.life_insurance_system.service.AuditLogService;
import com.life_insurance_system.model.Policy;
import com.life_insurance_system.service.PolicyService;
import com.life_insurance_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyDisputeService policyDisputeService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/staff")
    public String manageStaff(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/manage_staff";
    }

    @PostMapping("/staff/create")
    public String createStaff(User user, @RequestParam("roleId") int roleId, RedirectAttributes redirectAttributes) {
        userService.createStaffUser(user, roleId);
        redirectAttributes.addFlashAttribute("success", "Staff account created successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/deactivate/{id}")
    public String deactivateStaff(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.deactivateUser(id);
        redirectAttributes.addFlashAttribute("success", "Staff account deactivated successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/activate/{id}")
    public String activateStaff(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.activateUser(id);
        redirectAttributes.addFlashAttribute("success", "Staff account activated successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Staff account deleted successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/edit/{id}")
    public String editStaffPage(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/edit_staff";
    }

    @PostMapping("/staff/edit")
    public String editStaff(User user, @RequestParam("roleId") int roleId, @RequestParam(value = "newPassword", required = false) String newPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("user");
        User existingUser = userService.getUserById(user.getUserId());

        if (existingUser != null && existingUser.getRole().getRoleName().equals("HR/Admin Manager") && loggedInUser.getUserId() != existingUser.getUserId()) {
            redirectAttributes.addFlashAttribute("error", "Cannot edit another admin's account.");
            return "redirect:/admin/staff";
        }

        if (loggedInUser.getUserId() == user.getUserId() && loggedInUser.getRole().getRoleId() != roleId) {
            redirectAttributes.addFlashAttribute("error", "Admin cannot change their own role.");
            return "redirect:/admin/staff";
        }

        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            com.life_insurance_system.model.Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null) {
                existingUser.setRole(role);
            }
            userService.updateUser(existingUser, newPassword);
            redirectAttributes.addFlashAttribute("success", "Staff account updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        }

        return "redirect:/admin/staff";
    }

    @GetMapping("/disputes")
    public String manageDisputes(Model model) {
        model.addAttribute("disputes", policyDisputeService.getAllPolicyDisputes());
        model.addAttribute("newDispute", new PolicyDispute());
        model.addAttribute("policies", policyService.getAllPolicies());
        return "admin/manage_disputes";
    }

    @PostMapping("/disputes/create")
    public String createDispute(@ModelAttribute PolicyDispute newDispute, @RequestParam("policyId") int policyId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Policy policy = policyService.getPolicyById(policyId);
        if (policy != null) {
            newDispute.setPolicy(policy);
            newDispute.setAdmin(user);
            newDispute.setResolutionStatus(PolicyDispute.ResolutionStatus.Open);
            policyDisputeService.createPolicyDispute(newDispute);
            redirectAttributes.addFlashAttribute("success", "Dispute created successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Policy not found!");
        }
        return "redirect:/admin/disputes";
    }

    @GetMapping("/disputes/edit/{id}")
    public String editDisputePage(@PathVariable int id, Model model) {
        model.addAttribute("dispute", policyDisputeService.getPolicyDisputeById(id));
        return "admin/edit_dispute";
    }

    @PostMapping("/disputes/edit")
    public String editDispute(@ModelAttribute PolicyDispute dispute, RedirectAttributes redirectAttributes) {
        policyDisputeService.updatePolicyDispute(dispute);
        redirectAttributes.addFlashAttribute("success", "Dispute updated successfully!");
        return "redirect:/admin/disputes";
    }

    @GetMapping("/disputes/delete/{id}")
    public String deleteDispute(@PathVariable int id, RedirectAttributes redirectAttributes) {
        policyDisputeService.deletePolicyDispute(id);
        redirectAttributes.addFlashAttribute("success", "Dispute deleted successfully!");
        return "redirect:/admin/disputes";
    }
}
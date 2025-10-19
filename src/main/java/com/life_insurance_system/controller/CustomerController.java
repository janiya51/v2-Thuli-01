package com.life_insurance_system.controller;

import com.life_insurance_system.model.Application;
import com.life_insurance_system.model.Policy;
import com.life_insurance_system.model.User;
import com.life_insurance_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private SystemAnnouncementService systemAnnouncementService;

    @GetMapping("/dashboard")
    public String customerDashboard() {
        return "customer/dashboard";
    }

    @GetMapping("/apply-policy")
    public String applyForPolicy(Model model) {
        model.addAttribute("application", new Application());
        return "customer/apply_policy";
    }

    @PostMapping("/apply-policy")
    public String submitApplication(@ModelAttribute Application application, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        application.setUser(user);
        applicationService.createApplication(application);
        redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/policies")
    public String viewPolicies(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("policies", policyService.getPoliciesByUser(user));
        return "customer/view_policies";
    }

    @GetMapping("/policy/{id}")
    public String viewPolicyDetails(@PathVariable int id, Model model) {
        Policy policy = policyService.getPolicyById(id);
        if (policy != null) {
            model.addAttribute("policy", policy);
            model.addAttribute("payments", paymentService.getPaymentsByPolicy(policy));
            model.addAttribute("beneficiaries", beneficiaryService.getBeneficiariesByPolicy(policy));
        }
        return "customer/view_policy_detail";
    }

    @PostMapping("/policy/add-beneficiary")
    public String addBeneficiary(@ModelAttribute com.life_insurance_system.model.Beneficiary beneficiary, @RequestParam("policyId") int policyId, RedirectAttributes redirectAttributes) {
        Policy policy = policyService.getPolicyById(policyId);
        if (policy != null) {
            beneficiary.setPolicy(policy);
            beneficiaryService.createBeneficiary(beneficiary);
            redirectAttributes.addFlashAttribute("success", "Beneficiary added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Policy not found!");
        }
        return "redirect:/customer/policy/" + policyId;
    }

    @GetMapping("/policy/delete-beneficiary/{policyId}/{beneficiaryId}")
    public String deleteBeneficiary(@PathVariable int policyId, @PathVariable int beneficiaryId, RedirectAttributes redirectAttributes) {
        beneficiaryService.deleteBeneficiary(beneficiaryId);
        redirectAttributes.addFlashAttribute("success", "Beneficiary removed successfully!");
        return "redirect:/customer/policy/" + policyId;
    }

    @GetMapping("/file-claim")
    public String fileClaimPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("claim", new com.life_insurance_system.model.Claim());
        model.addAttribute("policies", policyService.getActivePoliciesByUser(user));
        return "customer/file_claim";
    }

    @PostMapping("/file-claim")
    public String fileClaim(@ModelAttribute com.life_insurance_system.model.Claim claim, @RequestParam("policyId") int policyId, RedirectAttributes redirectAttributes) {
        Policy policy = policyService.getPolicyById(policyId);
        if (policy != null) {
            claim.setPolicy(policy);
            claimService.createClaim(claim);
            redirectAttributes.addFlashAttribute("success", "Claim filed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Policy not found!");
        }
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/announcements")
    public String viewAnnouncements(Model model) {
        model.addAttribute("announcements", systemAnnouncementService.getAllAnnouncements());
        return "customer/view_announcements";
    }
}
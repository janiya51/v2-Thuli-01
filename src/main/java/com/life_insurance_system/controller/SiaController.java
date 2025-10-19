package com.life_insurance_system.controller;

import com.life_insurance_system.model.Application;
import com.life_insurance_system.model.RiskAssessment;
import com.life_insurance_system.model.User;
import com.life_insurance_system.service.ApplicationService;
import com.life_insurance_system.service.ClaimService;
import com.life_insurance_system.service.RiskAssessmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sia")
public class SiaController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @Autowired
    private ClaimService claimService;

    @GetMapping("/dashboard")
    public String siaDashboard() {
        return "sia/dashboard";
    }

    @GetMapping("/applications")
    public String viewSiaApplications(Model model) {
        model.addAttribute("applications", applicationService.getAllApplications());
        return "sia/view_applications";
    }

    @GetMapping("/assess/{id}")
    public String assessApplication(@PathVariable int id, Model model) {
        Application application = applicationService.getApplicationById(id);
        RiskAssessment riskAssessment = new RiskAssessment();
        riskAssessment.setApplication(application);
        model.addAttribute("riskAssessment", riskAssessment);
        return "sia/assess_application";
    }

    @PostMapping("/assess")
    public String submitAssessment(@ModelAttribute RiskAssessment riskAssessment, @RequestParam("applicationId") int applicationId, @RequestParam("status") String status, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Application application = applicationService.getApplicationById(applicationId);
        riskAssessment.setApplication(application);
        riskAssessment.setAdvisor(user);
        riskAssessmentService.createRiskAssessment(riskAssessment);

        if (application != null) {
            application.setCurrentStatus(Application.ApplicationStatus.valueOf(status));
            applicationService.updateApplication(application);
            redirectAttributes.addFlashAttribute("success", "Assessment submitted and application status updated!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Application not found!");
        }

        return "redirect:/sia/applications";
    }

    @GetMapping("/claims")
    public String viewSiaClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "sia/view_claims";
    }

    @GetMapping("/reports")
    public String viewSiaReports(Model model) {
        model.addAttribute("assessments", riskAssessmentService.getAllRiskAssessments());
        return "sia/view_reports";
    }

    @GetMapping("/claim/assess/{id}")
    public String assessClaimPage(@PathVariable int id, Model model) {
        model.addAttribute("claim", claimService.getClaimById(id));
        return "sia/assess_claim";
    }

    @PostMapping("/claim/assess")
    public String assessClaim(@ModelAttribute com.life_insurance_system.model.Claim claim, @RequestParam("claimId") int claimId, @RequestParam("claimStatus") String status, RedirectAttributes redirectAttributes) {
        com.life_insurance_system.model.Claim existingClaim = claimService.getClaimById(claimId);
        if (existingClaim != null) {
            existingClaim.setClaimStatus(com.life_insurance_system.model.Claim.ClaimStatus.valueOf(status));
            existingClaim.setPayoutAmount(claim.getPayoutAmount());
            claimService.updateClaim(existingClaim);
            redirectAttributes.addFlashAttribute("success", "Claim assessed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Claim not found!");
        }
        return "redirect:/sia/claims";
    }
}
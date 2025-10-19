package com.life_insurance_system.controller;

import com.life_insurance_system.model.Application;
import com.life_insurance_system.model.Payment;
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
@RequestMapping("/fo")
public class FoController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PremiumService premiumService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ClaimService claimService;

    @GetMapping("/dashboard")
    public String foDashboard() {
        return "fo/dashboard";
    }

    @GetMapping("/applications")
    public String viewFoApplications(Model model) {
        model.addAttribute("applications", applicationService.getAllApplications());
        return "fo/view_applications";
    }

    @GetMapping("/calculate-premium/{id}")
    public String calculatePremium(@PathVariable int id, Model model) {
        Application application = applicationService.getApplicationById(id);
        java.math.BigDecimal premium = premiumService.calculatePremium(application);
        model.addAttribute("application", application);
        model.addAttribute("premium", premium);
        return "fo/calculate_premium";
    }

    @PostMapping("/finalize-application")
    public String finalizeApplication(@RequestParam("applicationId") int applicationId, @RequestParam("annualPremium") java.math.BigDecimal annualPremium, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Application application = applicationService.getApplicationById(applicationId);
        if (application != null) {
            application.setCurrentStatus(Application.ApplicationStatus.PendingCustomer);
            applicationService.updateApplication(application);
            redirectAttributes.addFlashAttribute("success", "Premium calculated and application finalized!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Application not found!");
        }

        return "redirect:/fo/applications";
    }

    @GetMapping("/payments")
    public String processPayments(Model model) {
        model.addAttribute("policies", policyService.getAllPolicies());
        return "fo/process_payments";
    }

    @GetMapping("/payouts")
    public String processPayouts(Model model) {
        model.addAttribute("claims", claimService.getAllClaims().stream().filter(c -> c.getClaimStatus() == com.life_insurance_system.model.Claim.ClaimStatus.Approved).collect(java.util.stream.Collectors.toList()));
        return "fo/process_payouts";
    }

    @PostMapping("/record-payment")
    public String recordPayment(@RequestParam("policyId") int policyId, @RequestParam("amount") java.math.BigDecimal amount, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Policy policy = policyService.getPolicyById(policyId);
        if (policy != null) {
            Payment payment = new Payment();
            payment.setPolicy(policy);
            payment.setFinanceOfficer(user);
            payment.setAmount(amount);
            payment.setPaymentDate(new java.sql.Date(System.currentTimeMillis()));
            payment.setType(Payment.PaymentType.Received);
            payment.setStatus(Payment.PaymentStatus.Paid);
            paymentService.createPayment(payment);
            redirectAttributes.addFlashAttribute("success", "Payment recorded successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Policy not found!");
        }
        return "redirect:/fo/payments";
    }

    @PostMapping("/process-payout")
    public String processPayout(@RequestParam("claimId") int claimId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        com.life_insurance_system.model.Claim claim = claimService.getClaimById(claimId);
        if (claim != null) {
            claim.setClaimStatus(com.life_insurance_system.model.Claim.ClaimStatus.Paid);
            claimService.updateClaim(claim);

            Payment payment = new Payment();
            payment.setPolicy(claim.getPolicy());
            payment.setFinanceOfficer(user);
            payment.setAmount(claim.getPayoutAmount().negate());
            payment.setPaymentDate(new java.sql.Date(System.currentTimeMillis()));
            payment.setType(Payment.PaymentType.Received);
            payment.setStatus(Payment.PaymentStatus.Paid);
            paymentService.createPayment(payment);
            redirectAttributes.addFlashAttribute("success", "Payout processed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Claim not found!");
        }
        return "redirect:/fo/payouts";
    }

    @GetMapping("/payment-history")
    public String viewPaymentHistory(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "fo/view_payment_history";
    }
}
package com.life_insurance_system.controller;

import com.life_insurance_system.model.User;
import com.life_insurance_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.life_insurance_system.model.Application;
import com.life_insurance_system.model.CustomerDetail;
import com.life_insurance_system.model.RiskAssessment;
import com.life_insurance_system.service.ApplicationService;
import com.life_insurance_system.model.Payment;
import com.life_insurance_system.model.Policy;
import com.life_insurance_system.service.PaymentService;
import com.life_insurance_system.service.ClaimService;
import com.life_insurance_system.service.PolicyService;
import com.life_insurance_system.model.SystemAnnouncement;
import com.life_insurance_system.service.PremiumService;
import com.life_insurance_system.service.RiskAssessmentService;
import com.life_insurance_system.model.InternalMessage;
import com.life_insurance_system.service.BeneficiaryService;
import com.life_insurance_system.model.Beneficiary;
import com.life_insurance_system.model.Role;
import com.life_insurance_system.service.InternalMessageService;
import com.life_insurance_system.model.PolicyDispute;
import com.life_insurance_system.service.AuditLogService;
import com.life_insurance_system.service.CustomerDetailService;
import com.life_insurance_system.service.PolicyDisputeService;
import com.life_insurance_system.service.SystemAnnouncementService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @Autowired
    private PremiumService premiumService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private SystemAnnouncementService systemAnnouncementService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private InternalMessageService internalMessageService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private PolicyDisputeService policyDisputeService;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private com.life_insurance_system.repository.RoleRepository roleRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            String roleName = user.getRole().getRoleName();
            String path = switch (roleName) {
                case "Customer" -> "/customer/dashboard";
                case "Customer Service Executive" -> "/cse/dashboard";
                case "Senior Insurance Advisor" -> "/sia/dashboard";
                case "Finance Officer" -> "/fo/dashboard";
                case "IT System Analyst" -> "/isa/dashboard";
                case "HR/Admin Manager" -> "/admin/dashboard";
                default -> "/login";
            };
            return "redirect:" + path;
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(com.life_insurance_system.model.User user,
                           @RequestParam("dateOfBirth") String dateOfBirth,
                           @RequestParam("phoneNumber") String phoneNumber,
                           @RequestParam("address") String address,
                           RedirectAttributes redirectAttributes) {

        com.life_insurance_system.model.CustomerDetail customerDetail = new com.life_insurance_system.model.CustomerDetail();
        customerDetail.setDateOfBirth(java.sql.Date.valueOf(dateOfBirth));
        customerDetail.setPhoneNumber(phoneNumber);
        customerDetail.setAddress(address);

        userService.registerUser(user, customerDetail);

        redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }










    // Messaging Functionality
    @GetMapping("/messages/inbox")
    public String viewInbox(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        String roleName = user.getRole().getRoleName();
        String dashboardUrl = switch (roleName) {
            case "Customer" -> "/customer/dashboard";
            case "Customer Service Executive" -> "/cse/dashboard";
            case "Senior Insurance Advisor" -> "/sia/dashboard";
            case "Finance Officer" -> "/fo/dashboard";
            case "IT System Analyst" -> "/isa/dashboard";
            case "HR/Admin Manager" -> "/admin/dashboard";
            default -> "/login";
        };
        model.addAttribute("dashboardUrl", dashboardUrl);
        model.addAttribute("messages", internalMessageService.getInbox(user));
        return "messaging/inbox";
    }

    @GetMapping("/messages/sent")
    public String viewSentMessages(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("messages", internalMessageService.getSentMessages(user));
        return "messaging/sent";
    }

    @GetMapping("/messages/compose")
    public String composeMessage(Model model) {
        model.addAttribute("message", new InternalMessage());
        model.addAttribute("staff", userService.getAllUsers().stream().filter(u -> u.getRole().getRoleId() != 1).collect(java.util.stream.Collectors.toList()));
        return "messaging/compose_message";
    }

    @GetMapping("/messages/view/{id}")
    public String viewMessage(@PathVariable int id, Model model) {
        InternalMessage message = internalMessageService.getMessageById(id);
        if (message != null) {
            message.setRead(true);
            internalMessageService.sendMessage(message); // This will update
            model.addAttribute("message", message);
        }
        return "messaging/view_message";
    }

    @PostMapping("/messages/send")
    public String sendMessage(@ModelAttribute InternalMessage message, @RequestParam("receiverId") int receiverId, HttpSession session, RedirectAttributes redirectAttributes) {
        User sender = (User) session.getAttribute("user");
        if (sender == null) {
            return "redirect:/login";
        }
        User receiver = userService.getAllUsers().stream().filter(u -> u.getUserId() == receiverId).findFirst().orElse(null);
        if (receiver != null) {
            message.setSender(sender);
            message.setReceiver(receiver);
            internalMessageService.sendMessage(message);
            redirectAttributes.addFlashAttribute("success", "Message sent successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Receiver not found!");
        }
        return "redirect:/messages/inbox";
    }

    // Search Functionality
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        String roleName = user.getRole().getRoleName();
        String dashboardUrl = switch (roleName) {
            case "Customer" -> "/customer/dashboard";
            case "Customer Service Executive" -> "/cse/dashboard";
            case "Senior Insurance Advisor" -> "/sia/dashboard";
            case "Finance Officer" -> "/fo/dashboard";
            case "IT System Analyst" -> "/isa/dashboard";
            case "HR/Admin Manager" -> "/admin/dashboard";
            default -> "/login";
        };
        model.addAttribute("dashboardUrl", dashboardUrl);
        model.addAttribute("users", userService.searchUsers(keyword));
        model.addAttribute("roleName", user.getRole().getRoleName());
        return "search_results";
    }

    // Profile Management
    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        String roleName = user.getRole().getRoleName();
        String dashboardUrl = switch (roleName) {
            case "Customer" -> "/customer/dashboard";
            case "Customer Service Executive" -> "/cse/dashboard";
            case "Senior Insurance Advisor" -> "/sia/dashboard";
            case "Finance Officer" -> "/fo/dashboard";
            case "IT System Analyst" -> "/isa/dashboard";
            case "HR/Admin Manager" -> "/admin/dashboard";
            default -> "/login";
        };
        model.addAttribute("dashboardUrl", dashboardUrl);
        model.addAttribute("user", user);
        model.addAttribute("roleName", user.getRole().getRoleName());
        if (user.getRole().getRoleName().equals("Customer")) {
            model.addAttribute("customerDetail", customerDetailService.getCustomerDetailByUser(user));
        }
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, @RequestParam(value = "newPassword", required = false) String newPassword,
                                @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                @RequestParam(value = "address", required = false) String address,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Update user details
        sessionUser.setFirstName(user.getFirstName());
        sessionUser.setLastName(user.getLastName());
        sessionUser.setEmail(user.getEmail());

        userService.updateUser(sessionUser, newPassword);

        // Update customer details if applicable
        if (sessionUser.getRole().getRoleName().equals("Customer")) {
            CustomerDetail customerDetail = customerDetailService.getCustomerDetailByUser(sessionUser);
            if(customerDetail != null) {
                customerDetail.setPhoneNumber(phoneNumber);
                customerDetail.setAddress(address);
                customerDetail.setPendingReview(true);
                customerDetailService.updateCustomerDetail(customerDetail);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");

        return "redirect:/profile";
    }
}
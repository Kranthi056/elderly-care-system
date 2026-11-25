package com.elderlycare.controller;

import com.elderlycare.entity.*;
import com.elderlycare.repository.*;
import com.elderlycare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final CareServiceRepository careServiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final CareReportRepository careReportRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalElderly", userRepository.countByRole(Role.ELDERLY));
        model.addAttribute("totalCaregivers", userRepository.countByRole(Role.CAREGIVER));
        model.addAttribute("pendingApprovals", caregiverProfileRepository.countByVerificationStatus(VerificationStatus.PENDING));
        model.addAttribute("totalRequests", serviceRequestRepository.count());
        model.addAttribute("pendingRequests", serviceRequestRepository.countByStatus(RequestStatus.PENDING));
        model.addAttribute("activeAssignments", assignmentRepository.countByStatus(AssignmentStatus.IN_PROGRESS) + assignmentRepository.countByStatus(AssignmentStatus.ACCEPTED));
        model.addAttribute("completedServices", assignmentRepository.countByStatus(AssignmentStatus.COMPLETED));
        model.addAttribute("recentRequests", serviceRequestRepository.findAll().stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/elderly")
    public String manageElderly(Model model) {
        model.addAttribute("users", userRepository.findByRole(Role.ELDERLY));
        return "admin/elderly";
    }

    @GetMapping("/caregivers")
    public String manageCaregivers(Model model) {
        model.addAttribute("caregivers", caregiverProfileRepository.findAll());
        return "admin/caregivers";
    }

    @GetMapping("/verification")
    public String verification(Model model) {
        model.addAttribute("pending", caregiverProfileRepository.findByVerificationStatus(VerificationStatus.PENDING));
        model.addAttribute("approved", caregiverProfileRepository.findByVerificationStatus(VerificationStatus.APPROVED));
        model.addAttribute("rejected", caregiverProfileRepository.findByVerificationStatus(VerificationStatus.REJECTED));
        return "admin/verification";
    }

    @PostMapping("/verification/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        CaregiverProfile p = caregiverProfileRepository.findById(id).orElseThrow();
        p.setVerificationStatus(VerificationStatus.APPROVED);
        p.setAdminRemarks("Approved by administrator");
        caregiverProfileRepository.save(p);
        notificationRepository.save(Notification.builder()
                .user(p.getUser())
                .title("Account Verified")
                .message("Congratulations! Your caregiver account has been approved.")
                .type("SUCCESS")
                .build());
        ra.addFlashAttribute("message", "Caregiver approved.");
        return "redirect:/admin/verification";
    }

    @PostMapping("/verification/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(required = false) String reason, RedirectAttributes ra) {
        CaregiverProfile p = caregiverProfileRepository.findById(id).orElseThrow();
        p.setVerificationStatus(VerificationStatus.REJECTED);
        p.setAdminRemarks(reason != null ? reason : "Application rejected");
        caregiverProfileRepository.save(p);
        notificationRepository.save(Notification.builder()
                .user(p.getUser())
                .title("Verification Rejected")
                .message("Your caregiver application was not approved. Reason: " + p.getAdminRemarks())
                .type("WARNING")
                .build());
        ra.addFlashAttribute("message", "Caregiver rejected.");
        return "redirect:/admin/verification";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", careServiceRepository.findAll());
        return "admin/services";
    }

    @PostMapping("/services/add")
    public String addService(@RequestParam String name, @RequestParam String description,
                             @RequestParam(required = false) String iconClass,
                             @RequestParam(required = false) Double basePrice, RedirectAttributes ra) {
        CareService s = CareService.builder()
                .name(name)
                .description(description)
                .iconClass(iconClass != null ? iconClass : "fa-hand-holding-heart")
                .basePrice(basePrice)
                .active(true)
                .build();
        careServiceRepository.save(s);
        ra.addFlashAttribute("message", "Service added.");
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/toggle")
    public String toggleService(@PathVariable Long id) {
        CareService s = careServiceRepository.findById(id).orElseThrow();
        s.setActive(!s.isActive());
        careServiceRepository.save(s);
        return "redirect:/admin/services";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        model.addAttribute("requests", serviceRequestRepository.findAll());
        model.addAttribute("caregivers", caregiverProfileRepository.findByVerificationStatus(VerificationStatus.APPROVED));
        return "admin/requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id, RedirectAttributes ra) {
        ServiceRequest r = serviceRequestRepository.findById(id).orElseThrow();
        r.setStatus(RequestStatus.APPROVED);
        serviceRequestRepository.save(r);
        ra.addFlashAttribute("message", "Request approved.");
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes ra) {
        ServiceRequest r = serviceRequestRepository.findById(id).orElseThrow();
        r.setStatus(RequestStatus.REJECTED);
        serviceRequestRepository.save(r);
        ra.addFlashAttribute("message", "Request rejected.");
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/{id}/assign")
    public String assignCaregiver(@PathVariable Long id, @RequestParam Long caregiverId,
                                  @RequestParam String scheduledDate, @RequestParam String scheduledTime,
                                  RedirectAttributes ra) {
        ServiceRequest r = serviceRequestRepository.findById(id).orElseThrow();
        User caregiver = userRepository.findById(caregiverId).orElseThrow();
        Assignment a = Assignment.builder()
                .serviceRequest(r)
                .caregiver(caregiver)
                .elderlyUser(r.getElderlyUser())
                .scheduledDate(LocalDate.parse(scheduledDate))
                .scheduledTime(LocalTime.parse(scheduledTime))
                .status(AssignmentStatus.PENDING)
                .build();
        assignmentRepository.save(a);
        r.setStatus(RequestStatus.ASSIGNED);
        serviceRequestRepository.save(r);

        notificationRepository.save(Notification.builder()
                .user(caregiver)
                .title("New Assignment")
                .message("You have a new care assignment for " + r.getElderlyUser().getFullName())
                .type("INFO")
                .build());
        notificationRepository.save(Notification.builder()
                .user(r.getElderlyUser())
                .title("Caregiver Assigned")
                .message(caregiver.getFullName() + " has been assigned to your request.")
                .type("SUCCESS")
                .build());

        ra.addFlashAttribute("message", "Caregiver assigned successfully.");
        return "redirect:/admin/requests";
    }

    @GetMapping("/assignments")
    public String assignments(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        return "admin/assignments";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("reports", careReportRepository.findAll());
        return "admin/reports";
    }

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User admin = userService.findByEmail(auth.getName());
        model.addAttribute("notifications", notificationRepository.findByUserOrderByCreatedAtDesc(admin));
        return "admin/notifications";
    }
}

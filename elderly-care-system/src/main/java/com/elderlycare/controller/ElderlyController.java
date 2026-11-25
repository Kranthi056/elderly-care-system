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
@RequestMapping("/elderly")
@RequiredArgsConstructor
public class ElderlyController {

    private final UserService userService;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final CareServiceRepository careServiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final CareReportRepository careReportRepository;
    private final NotificationRepository notificationRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;

    private User currentUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = currentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("profile", elderlyProfileRepository.findByUser(user).orElse(null));
        model.addAttribute("requests", serviceRequestRepository.findByElderlyUser(user));
        model.addAttribute("assignments", assignmentRepository.findByElderlyUser(user));
        model.addAttribute("notifications", notificationRepository.findByUserOrderByCreatedAtDesc(user));
        model.addAttribute("unreadCount", notificationRepository.countByUserAndReadFalse(user));
        return "elderly/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = currentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("profile", elderlyProfileRepository.findByUser(user).orElse(new ElderlyProfile()));
        return "elderly/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication auth, @ModelAttribute User formUser,
                                @ModelAttribute ElderlyProfile formProfile, RedirectAttributes ra) {
        User user = currentUser(auth);
        user.setFullName(formUser.getFullName());
        user.setPhone(formUser.getPhone());
        user.setAddress(formUser.getAddress());
        user.setCity(formUser.getCity());
        user.setState(formUser.getState());
        user.setZipCode(formUser.getZipCode());
        userService.save(user);

        ElderlyProfile profile = elderlyProfileRepository.findByUser(user).orElse(new ElderlyProfile());
        profile.setUser(user);
        profile.setDateOfBirth(formProfile.getDateOfBirth());
        profile.setGender(formProfile.getGender());
        profile.setBloodGroup(formProfile.getBloodGroup());
        profile.setEmergencyContactName(formProfile.getEmergencyContactName());
        profile.setEmergencyContactPhone(formProfile.getEmergencyContactPhone());
        profile.setEmergencyContactRelation(formProfile.getEmergencyContactRelation());
        profile.setPreferredLanguage(formProfile.getPreferredLanguage());
        elderlyProfileRepository.save(profile);

        ra.addFlashAttribute("message", "Profile updated successfully.");
        return "redirect:/elderly/profile";
    }

    @GetMapping("/medical")
    public String medical(Authentication auth, Model model) {
        User user = currentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("profile", elderlyProfileRepository.findByUser(user).orElse(new ElderlyProfile()));
        return "elderly/medical";
    }

    @PostMapping("/medical")
    public String updateMedical(Authentication auth, @ModelAttribute ElderlyProfile formProfile, RedirectAttributes ra) {
        User user = currentUser(auth);
        ElderlyProfile profile = elderlyProfileRepository.findByUser(user).orElse(new ElderlyProfile());
        profile.setUser(user);
        profile.setMedicalHistory(formProfile.getMedicalHistory());
        profile.setHealthConditions(formProfile.getHealthConditions());
        profile.setAllergies(formProfile.getAllergies());
        profile.setCurrentMedications(formProfile.getCurrentMedications());
        profile.setCarePreferences(formProfile.getCarePreferences());
        elderlyProfileRepository.save(profile);
        ra.addFlashAttribute("message", "Medical information updated.");
        return "redirect:/elderly/medical";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", careServiceRepository.findByActiveTrue());
        return "elderly/services";
    }

    @GetMapping("/request")
    public String requestForm(Model model) {
        model.addAttribute("services", careServiceRepository.findByActiveTrue());
        model.addAttribute("request", new ServiceRequest());
        return "elderly/request-service";
    }

    @PostMapping("/request")
    public String submitRequest(Authentication auth,
                                @RequestParam Long serviceId,
                                @RequestParam String description,
                                @RequestParam String preferredDate,
                                @RequestParam String preferredTime,
                                @RequestParam String location,
                                @RequestParam(required = false) String additionalRequirements,
                                RedirectAttributes ra) {
        User user = currentUser(auth);
        CareService service = careServiceRepository.findById(serviceId).orElseThrow();
        ServiceRequest req = ServiceRequest.builder()
                .elderlyUser(user)
                .service(service)
                .description(description)
                .preferredDate(LocalDate.parse(preferredDate))
                .preferredTime(LocalTime.parse(preferredTime))
                .location(location)
                .additionalRequirements(additionalRequirements)
                .status(RequestStatus.PENDING)
                .build();
        serviceRequestRepository.save(req);
        ra.addFlashAttribute("message", "Service request submitted successfully! Our team will review it shortly.");
        return "redirect:/elderly/requests";
    }

    @GetMapping("/requests")
    public String myRequests(Authentication auth, Model model) {
        User user = currentUser(auth);
        model.addAttribute("requests", serviceRequestRepository.findByElderlyUser(user));
        return "elderly/requests";
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancelRequest(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        ServiceRequest req = serviceRequestRepository.findById(id).orElseThrow();
        if (req.getElderlyUser().getId().equals(currentUser(auth).getId())
                && (req.getStatus() == RequestStatus.PENDING || req.getStatus() == RequestStatus.APPROVED)) {
            req.setStatus(RequestStatus.CANCELLED);
            serviceRequestRepository.save(req);
            ra.addFlashAttribute("message", "Request cancelled.");
        }
        return "redirect:/elderly/requests";
    }

    @GetMapping("/caregiver")
    public String myCaregiver(Authentication auth, Model model) {
        User user = currentUser(auth);
        List<Assignment> assignments = assignmentRepository.findByElderlyUser(user);
        Assignment active = assignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.ACCEPTED || a.getStatus() == AssignmentStatus.IN_PROGRESS)
                .findFirst().orElse(null);
        model.addAttribute("assignment", active);
        if (active != null) {
            model.addAttribute("caregiverProfile", caregiverProfileRepository.findByUser(active.getCaregiver()).orElse(null));
        }
        return "elderly/caregiver";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model) {
        model.addAttribute("assignments", assignmentRepository.findByElderlyUser(currentUser(auth)));
        return "elderly/schedule";
    }

    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        model.addAttribute("reports", careReportRepository.findByElderlyUser(currentUser(auth)));
        return "elderly/reports";
    }

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User user = currentUser(auth);
        List<Notification> notes = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        notes.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notes);
        model.addAttribute("notifications", notes);
        return "elderly/notifications";
    }

    @GetMapping("/messages")
    public String messages(Authentication auth, Model model) {
        model.addAttribute("user", currentUser(auth));
        return "elderly/messages";
    }

    @GetMapping("/feedback")
    public String feedback(Authentication auth, Model model) {
        model.addAttribute("user", currentUser(auth));
        model.addAttribute("assignments", assignmentRepository.findByElderlyUser(currentUser(auth)));
        return "elderly/feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(Authentication auth,
                                 @RequestParam Integer rating,
                                 @RequestParam(required = false) String comments,
                                 RedirectAttributes ra) {
        User user = currentUser(auth);
        notificationRepository.save(Notification.builder()
                .user(user)
                .title("Feedback Submitted")
                .message("Thank you for your " + rating + "-star feedback. We appreciate your input.")
                .type("SUCCESS")
                .build());
        ra.addFlashAttribute("message", "Thank you! Your feedback has been submitted.");
        return "redirect:/elderly/feedback";
    }
}

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
@RequestMapping("/caregiver")
@RequiredArgsConstructor
public class CaregiverController {

    private final UserService userService;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final AssignmentRepository assignmentRepository;
    private final CareReportRepository careReportRepository;
    private final NotificationRepository notificationRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;

    private User currentUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = currentUser(auth);
        CaregiverProfile profile = caregiverProfileRepository.findByUser(user).orElse(null);
        List<Assignment> assignments = assignmentRepository.findByCaregiver(user);
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("assignments", assignments);
        model.addAttribute("totalAssignments", assignments.size());
        model.addAttribute("activeCount", assignments.stream().filter(a -> a.getStatus() == AssignmentStatus.ACCEPTED || a.getStatus() == AssignmentStatus.IN_PROGRESS).count());
        model.addAttribute("completedCount", assignments.stream().filter(a -> a.getStatus() == AssignmentStatus.COMPLETED).count());
        model.addAttribute("notifications", notificationRepository.findByUserOrderByCreatedAtDesc(user));
        return "caregiver/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = currentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("profile", caregiverProfileRepository.findByUser(user).orElse(new CaregiverProfile()));
        return "caregiver/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication auth, @ModelAttribute User formUser,
                                @ModelAttribute CaregiverProfile formProfile, RedirectAttributes ra) {
        User user = currentUser(auth);
        user.setFullName(formUser.getFullName());
        user.setPhone(formUser.getPhone());
        user.setAddress(formUser.getAddress());
        user.setCity(formUser.getCity());
        user.setState(formUser.getState());
        user.setZipCode(formUser.getZipCode());
        userService.save(user);

        CaregiverProfile profile = caregiverProfileRepository.findByUser(user).orElse(new CaregiverProfile());
        profile.setUser(user);
        profile.setQualifications(formProfile.getQualifications());
        profile.setYearsOfExperience(formProfile.getYearsOfExperience());
        profile.setSkills(formProfile.getSkills());
        profile.setServiceLocations(formProfile.getServiceLocations());
        profile.setAvailability(formProfile.getAvailability());
        profile.setHourlyRate(formProfile.getHourlyRate());
        caregiverProfileRepository.save(profile);

        ra.addFlashAttribute("message", "Profile updated successfully.");
        return "redirect:/caregiver/profile";
    }

    @GetMapping("/verification")
    public String verification(Authentication auth, Model model) {
        User user = currentUser(auth);
        model.addAttribute("profile", caregiverProfileRepository.findByUser(user).orElse(null));
        return "caregiver/verification";
    }

    @GetMapping("/assignments")
    public String assignments(Authentication auth, Model model) {
        model.addAttribute("assignments", assignmentRepository.findByCaregiver(currentUser(auth)));
        return "caregiver/assignments";
    }

    @PostMapping("/assignments/{id}/accept")
    public String accept(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Assignment a = assignmentRepository.findById(id).orElseThrow();
        if (a.getCaregiver().getId().equals(currentUser(auth).getId())) {
            a.setStatus(AssignmentStatus.ACCEPTED);
            assignmentRepository.save(a);
            ra.addFlashAttribute("message", "Assignment accepted.");
        }
        return "redirect:/caregiver/assignments";
    }

    @PostMapping("/assignments/{id}/reject")
    public String reject(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Assignment a = assignmentRepository.findById(id).orElseThrow();
        if (a.getCaregiver().getId().equals(currentUser(auth).getId())) {
            a.setStatus(AssignmentStatus.REJECTED);
            assignmentRepository.save(a);
            ra.addFlashAttribute("message", "Assignment rejected.");
        }
        return "redirect:/caregiver/assignments";
    }

    @PostMapping("/assignments/{id}/start")
    public String start(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Assignment a = assignmentRepository.findById(id).orElseThrow();
        if (a.getCaregiver().getId().equals(currentUser(auth).getId())) {
            a.setStatus(AssignmentStatus.IN_PROGRESS);
            a.setStartedAt(java.time.LocalDateTime.now());
            assignmentRepository.save(a);
            ra.addFlashAttribute("message", "Visit started.");
        }
        return "redirect:/caregiver/assignments";
    }

    @GetMapping("/elderly/{id}")
    public String elderlyDetails(@PathVariable Long id, Authentication auth, Model model) {
        User elderly = userService.findById(id);
        model.addAttribute("elderly", elderly);
        model.addAttribute("profile", elderlyProfileRepository.findByUser(elderly).orElse(null));
        return "caregiver/elderly-details";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model) {
        model.addAttribute("assignments", assignmentRepository.findByCaregiver(currentUser(auth)));
        return "caregiver/schedule";
    }

    @GetMapping("/report")
    public String reportForm(Authentication auth, Model model) {
        List<Assignment> active = assignmentRepository.findByCaregiver(currentUser(auth)).stream()
                .filter(a -> a.getStatus() == AssignmentStatus.IN_PROGRESS || a.getStatus() == AssignmentStatus.ACCEPTED)
                .toList();
        model.addAttribute("assignments", active);
        model.addAttribute("report", new CareReport());
        return "caregiver/report";
    }

    @PostMapping("/report")
    public String submitReport(Authentication auth,
                               @RequestParam Long assignmentId,
                               @RequestParam String visitDate,
                               @RequestParam String startTime,
                               @RequestParam String endTime,
                               @RequestParam String completedActivities,
                               @RequestParam(required = false) String pendingActivities,
                               @RequestParam(required = false) String healthObservations,
                               @RequestParam(required = false) String remarks,
                               RedirectAttributes ra) {
        User caregiver = currentUser(auth);
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        CareReport report = CareReport.builder()
                .assignment(assignment)
                .caregiver(caregiver)
                .elderlyUser(assignment.getElderlyUser())
                .visitDate(LocalDate.parse(visitDate))
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .completedActivities(completedActivities)
                .pendingActivities(pendingActivities)
                .healthObservations(healthObservations)
                .remarks(remarks)
                .build();
        careReportRepository.save(report);
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(java.time.LocalDateTime.now());
        assignmentRepository.save(assignment);
        ra.addFlashAttribute("message", "Care report submitted successfully.");
        return "redirect:/caregiver/reports";
    }

    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        model.addAttribute("reports", careReportRepository.findByCaregiver(currentUser(auth)));
        return "caregiver/reports";
    }

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User user = currentUser(auth);
        List<Notification> notes = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        notes.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notes);
        model.addAttribute("notifications", notes);
        return "caregiver/notifications";
    }
}

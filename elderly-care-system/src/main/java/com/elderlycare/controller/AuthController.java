package com.elderlycare.controller;

import com.elderlycare.entity.CaregiverProfile;
import com.elderlycare.entity.ElderlyProfile;
import com.elderlycare.entity.User;
import com.elderlycare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out successfully.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerOptions() {
        return "auth/register-options";
    }

    @GetMapping("/register/elderly")
    public String registerElderlyForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("profile", new ElderlyProfile());
        return "auth/register-elderly";
    }

    @PostMapping("/register/elderly")
    public String registerElderly(@ModelAttribute User user,
                                  @ModelAttribute ElderlyProfile profile,
                                  RedirectAttributes ra) {
        if (userService.emailExists(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register/elderly";
        }
        userService.registerElderly(user, profile);
        ra.addFlashAttribute("message", "Registration successful! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/register/caregiver")
    public String registerCaregiverForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("profile", new CaregiverProfile());
        return "auth/register-caregiver";
    }

    @PostMapping("/register/caregiver")
    public String registerCaregiver(@ModelAttribute User user,
                                    @ModelAttribute CaregiverProfile profile,
                                    RedirectAttributes ra) {
        if (userService.emailExists(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register/caregiver";
        }
        userService.registerCaregiver(user, profile);
        ra.addFlashAttribute("message", "Registration successful! Your account is pending verification. Please login.");
        return "redirect:/login";
    }
}

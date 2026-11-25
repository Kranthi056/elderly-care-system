package com.elderlycare.controller;

import com.elderlycare.entity.CareService;
import com.elderlycare.entity.Role;
import com.elderlycare.entity.User;
import com.elderlycare.repository.CareServiceRepository;
import com.elderlycare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CareServiceRepository careServiceRepository;
    private final UserService userService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<CareService> services = careServiceRepository.findByActiveTrue();
        model.addAttribute("services", services);
        return "public/home";
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", careServiceRepository.findByActiveTrue());
        return "public/services";
    }

    @GetMapping("/how-it-works")
    public String howItWorks() {
        return "public/how-it-works";
    }

    @GetMapping("/contact")
    public String contact() {
        return "public/contact";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CAREGIVER"))) {
            return "redirect:/caregiver/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ELDERLY"))) {
            return "redirect:/elderly/dashboard";
        }
        return "redirect:/login";
    }
}

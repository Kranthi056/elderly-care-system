package com.elderlycare.service;

import com.elderlycare.entity.*;
import com.elderlycare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User registerElderly(User user, ElderlyProfile profile) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ELDERLY);
        user.setEnabled(true);
        User saved = userRepository.save(user);
        profile.setUser(saved);
        elderlyProfileRepository.save(profile);
        return saved;
    }

    @Transactional
    public User registerCaregiver(User user, CaregiverProfile profile) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CAREGIVER);
        user.setEnabled(true);
        User saved = userRepository.save(user);
        profile.setUser(saved);
        profile.setVerificationStatus(VerificationStatus.PENDING);
        caregiverProfileRepository.save(profile);
        return saved;
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}

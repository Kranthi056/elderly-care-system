package com.elderlycare.config;

import com.elderlycare.entity.*;
import com.elderlycare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final CareServiceRepository careServiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Admin
        User admin = User.builder()
                .email("admin@elderlycare.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .phone("9876543210")
                .role(Role.ADMIN)
                .enabled(true)
                .profileCompleted(true)
                .build();
        userRepository.save(admin);

        // Elderly Users
        User elderly1 = User.builder()
                .email("margaret@example.com")
                .password(passwordEncoder.encode("elderly123"))
                .fullName("Margaret Thompson")
                .phone("9123456780")
                .address("42 Maple Street")
                .city("Springfield")
                .state("IL")
                .zipCode("62701")
                .role(Role.ELDERLY)
                .enabled(true)
                .profileCompleted(true)
                .build();
        userRepository.save(elderly1);

        ElderlyProfile ep1 = ElderlyProfile.builder()
                .user(elderly1)
                .dateOfBirth(LocalDate.of(1945, 3, 15))
                .gender("Female")
                .bloodGroup("O+")
                .emergencyContactName("Robert Thompson")
                .emergencyContactPhone("9123456781")
                .emergencyContactRelation("Son")
                .medicalHistory("Hypertension, mild arthritis")
                .healthConditions("High blood pressure, joint pain")
                .allergies("Penicillin")
                .currentMedications("Amlodipine 5mg daily, Ibuprofen as needed")
                .carePreferences("Prefers morning visits, gentle assistance")
                .preferredLanguage("English")
                .build();
        elderlyProfileRepository.save(ep1);

        User elderly2 = User.builder()
                .email("james@example.com")
                .password(passwordEncoder.encode("elderly123"))
                .fullName("James Wilson")
                .phone("9234567890")
                .address("15 Oak Avenue")
                .city("Springfield")
                .state("IL")
                .zipCode("62702")
                .role(Role.ELDERLY)
                .enabled(true)
                .profileCompleted(true)
                .build();
        userRepository.save(elderly2);

        ElderlyProfile ep2 = ElderlyProfile.builder()
                .user(elderly2)
                .dateOfBirth(LocalDate.of(1938, 7, 22))
                .gender("Male")
                .bloodGroup("A+")
                .emergencyContactName("Susan Wilson")
                .emergencyContactPhone("9234567891")
                .emergencyContactRelation("Daughter")
                .medicalHistory("Diabetes Type 2, previous hip replacement")
                .healthConditions("Diabetes, limited mobility")
                .allergies("None known")
                .currentMedications("Metformin 500mg twice daily")
                .carePreferences("Needs help with mobility and meals")
                .preferredLanguage("English")
                .build();
        elderlyProfileRepository.save(ep2);

        // Caregivers
        User caregiver1 = User.builder()
                .email("sarah.care@example.com")
                .password(passwordEncoder.encode("caregiver123"))
                .fullName("Sarah Mitchell")
                .phone("9345678901")
                .address("88 Pine Road")
                .city("Springfield")
                .state("IL")
                .zipCode("62703")
                .role(Role.CAREGIVER)
                .enabled(true)
                .profileCompleted(true)
                .build();
        userRepository.save(caregiver1);

        CaregiverProfile cp1 = CaregiverProfile.builder()
                .user(caregiver1)
                .qualifications("Certified Nursing Assistant (CNA), First Aid & CPR")
                .yearsOfExperience(8)
                .skills("Medication management, Mobility assistance, Meal preparation, Companionship")
                .serviceLocations("Springfield, IL and surrounding areas")
                .availability("Monday-Friday 8AM-6PM, Saturdays by appointment")
                .certificateInfo("CNA License #IL-45892, Valid until 2027")
                .hourlyRate(25.0)
                .verificationStatus(VerificationStatus.APPROVED)
                .averageRating(4.8)
                .totalReviews(24)
                .build();
        caregiverProfileRepository.save(cp1);

        User caregiver2 = User.builder()
                .email("david.care@example.com")
                .password(passwordEncoder.encode("caregiver123"))
                .fullName("David Chen")
                .phone("9456789012")
                .address("22 Cedar Lane")
                .city("Springfield")
                .state("IL")
                .zipCode("62704")
                .role(Role.CAREGIVER)
                .enabled(true)
                .profileCompleted(true)
                .build();
        userRepository.save(caregiver2);

        CaregiverProfile cp2 = CaregiverProfile.builder()
                .user(caregiver2)
                .qualifications("Home Health Aide, Geriatric Care Certificate")
                .yearsOfExperience(5)
                .skills("Personal care, Health monitoring, Hospital accompaniment, Companionship")
                .serviceLocations("Springfield metro area")
                .availability("Flexible, including evenings and weekends")
                .certificateInfo("HHA Certificate #GH-3391")
                .hourlyRate(22.0)
                .verificationStatus(VerificationStatus.APPROVED)
                .averageRating(4.6)
                .totalReviews(15)
                .build();
        caregiverProfileRepository.save(cp2);

        User caregiver3 = User.builder()
                .email("pending.care@example.com")
                .password(passwordEncoder.encode("caregiver123"))
                .fullName("Emily Rodriguez")
                .phone("9567890123")
                .role(Role.CAREGIVER)
                .enabled(true)
                .profileCompleted(false)
                .build();
        userRepository.save(caregiver3);

        CaregiverProfile cp3 = CaregiverProfile.builder()
                .user(caregiver3)
                .qualifications("Nursing student, First Aid certified")
                .yearsOfExperience(2)
                .skills("Companionship, Basic assistance")
                .serviceLocations("Springfield")
                .availability("Weekends")
                .verificationStatus(VerificationStatus.PENDING)
                .build();
        caregiverProfileRepository.save(cp3);

        // Care Services
        CareService s1 = CareService.builder().name("Medication Assistance").description("Help with organizing and reminding about medications, ensuring proper dosage and timing.").iconClass("fa-pills").active(true).basePrice(30.0).build();
        CareService s2 = CareService.builder().name("Health Monitoring").description("Regular checks of blood pressure, blood sugar, temperature and general wellness observation.").iconClass("fa-heartbeat").active(true).basePrice(35.0).build();
        CareService s3 = CareService.builder().name("Mobility Assistance").description("Support with walking, transferring, and light physical activity to maintain independence.").iconClass("fa-walking").active(true).basePrice(40.0).build();
        CareService s4 = CareService.builder().name("Meal Assistance").description("Help with meal planning, preparation, and feeding if required. Special dietary needs supported.").iconClass("fa-utensils").active(true).basePrice(35.0).build();
        CareService s5 = CareService.builder().name("Hospital Appointment Assistance").description("Accompaniment to medical appointments, help with transportation and communication with doctors.").iconClass("fa-hospital").active(true).basePrice(50.0).build();
        CareService s6 = CareService.builder().name("Companionship").description("Friendly conversation, activities, reading, and emotional support to reduce loneliness.").iconClass("fa-users").active(true).basePrice(25.0).build();
        careServiceRepository.save(s1);
        careServiceRepository.save(s2);
        careServiceRepository.save(s3);
        careServiceRepository.save(s4);
        careServiceRepository.save(s5);
        careServiceRepository.save(s6);

        // Sample Service Request
        ServiceRequest req1 = ServiceRequest.builder()
                .elderlyUser(elderly1)
                .service(s1)
                .description("Need help organizing weekly medications and morning reminders.")
                .preferredDate(LocalDate.now().plusDays(2))
                .preferredTime(LocalTime.of(9, 0))
                .location("42 Maple Street, Springfield")
                .additionalRequirements("Prefer female caregiver if possible")
                .status(RequestStatus.ASSIGNED)
                .build();
        serviceRequestRepository.save(req1);

        ServiceRequest req2 = ServiceRequest.builder()
                .elderlyUser(elderly2)
                .service(s3)
                .description("Assistance with walking and getting out of bed.")
                .preferredDate(LocalDate.now().plusDays(1))
                .preferredTime(LocalTime.of(10, 30))
                .location("15 Oak Avenue, Springfield")
                .status(RequestStatus.PENDING)
                .build();
        serviceRequestRepository.save(req2);

        // Assignment
        Assignment a1 = Assignment.builder()
                .serviceRequest(req1)
                .caregiver(caregiver1)
                .elderlyUser(elderly1)
                .scheduledDate(LocalDate.now().plusDays(2))
                .scheduledTime(LocalTime.of(9, 0))
                .notes("First visit - introduce and set up medication system")
                .status(AssignmentStatus.ACCEPTED)
                .build();
        assignmentRepository.save(a1);

        // Notifications
        notificationRepository.save(Notification.builder()
                .user(elderly1)
                .title("Caregiver Assigned")
                .message("Sarah Mitchell has been assigned to your Medication Assistance request.")
                .type("SUCCESS")
                .build());
        notificationRepository.save(Notification.builder()
                .user(caregiver1)
                .title("New Assignment")
                .message("You have been assigned to Margaret Thompson for Medication Assistance.")
                .type("INFO")
                .build());
        notificationRepository.save(Notification.builder()
                .user(admin)
                .title("Pending Verification")
                .message("New caregiver Emily Rodriguez is awaiting verification.")
                .type("WARNING")
                .build());

        System.out.println("=== Default Data Initialized ===");
        System.out.println("Admin: admin@elderlycare.com / admin123");
        System.out.println("Elderly: margaret@example.com / elderly123");
        System.out.println("Elderly: james@example.com / elderly123");
        System.out.println("Caregiver: sarah.care@example.com / caregiver123");
        System.out.println("Caregiver: david.care@example.com / caregiver123");
        System.out.println("Pending Caregiver: pending.care@example.com / caregiver123");
    }
}

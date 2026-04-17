package me.ifmo.backend.security;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.User;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.PaymentRepository;
import me.ifmo.backend.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accessService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessService {

    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public boolean canAccessEnrollment(Long enrollmentId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }

        return enrollmentRepository.findById(enrollmentId)
                .map(Enrollment::getUser)
                .map(User::getEmail)
                .filter(email -> email.equals(authentication.getName()))
                .isPresent();
    }

    public boolean canAccessUserEnrollments(Long userId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }

        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }

    public boolean canCreatePaymentForEnrollment(Long enrollmentId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }

        return enrollmentRepository.findById(enrollmentId)
                .map(Enrollment::getUser)
                .map(User::getEmail)
                .filter(email -> email.equals(authentication.getName()))
                .isPresent();
    }

    public boolean canAccessPayment(Long paymentId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }

        return paymentRepository.findById(paymentId)
                .map(Payment::getEnrollment)
                .map(Enrollment::getUser)
                .map(User::getEmail)
                .filter(email -> email.equals(authentication.getName()))
                .isPresent();
    }

    public boolean canAccessPaymentByEnrollment(Long enrollmentId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }

        return enrollmentRepository.findById(enrollmentId)
                .map(Enrollment::getUser)
                .map(User::getEmail)
                .filter(email -> email.equals(authentication.getName()))
                .isPresent();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
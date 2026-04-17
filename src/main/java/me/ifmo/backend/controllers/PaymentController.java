package me.ifmo.backend.controllers;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.payment.PaymentDTO;
import me.ifmo.backend.mappers.PaymentMapper;
import me.ifmo.backend.services.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE') and @accessService.canCreatePaymentForEnrollment(#enrollmentId, authentication)")
    public PaymentDTO createPayment(@PathVariable @Min(1) Long enrollmentId) {
        return paymentMapper.toPaymentDTO(paymentService.createPayment(enrollmentId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_READ_ALL') or @accessService.canAccessPayment(#id, authentication)")
    public PaymentDTO getPaymentById(@PathVariable @Min(1) Long id) {
        return paymentMapper.toPaymentDTO(paymentService.getPaymentById(id));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAuthority('PAYMENT_READ_ALL') or @accessService.canAccessPaymentByEnrollment(#enrollmentId, authentication)")
    public PaymentDTO getPaymentByEnrollmentId(@PathVariable @Min(1) Long enrollmentId) {
        return paymentMapper.toPaymentDTO(paymentService.getPaymentByEnrollmentId(enrollmentId));
    }
}
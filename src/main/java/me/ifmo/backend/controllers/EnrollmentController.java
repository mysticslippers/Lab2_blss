package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentRequest;
import me.ifmo.backend.DTO.enrollment.EnrollmentDTO;
import me.ifmo.backend.mappers.EnrollmentMapper;
import me.ifmo.backend.services.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
@Validated
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    @PostMapping
    public EnrollmentDTO createEnrollment(@RequestBody @Valid CreateEnrollmentRequest request) {
        return enrollmentMapper.toEnrollmentDTO(
                enrollmentService.createEnrollment(request.getUserId(), request.getCourseId())
        );
    }

    @GetMapping("/{id}")
    public EnrollmentDTO getEnrollmentById(@PathVariable @Min(1) Long id) {
        return enrollmentMapper.toEnrollmentDTO(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/user/{userId}")
    public Page<EnrollmentDTO> getEnrollmentsByUserId(@PathVariable @Min(1) Long userId, Pageable pageable) {
        return enrollmentService.getEnrollmentsByUserId(userId, pageable)
                .map(enrollmentMapper::toEnrollmentDTO);
    }

    @GetMapping("/course/{courseId}")
    public Page<EnrollmentDTO> getEnrollmentsByCourseId(@PathVariable @Min(1) Long courseId, Pageable pageable) {
        return enrollmentService.getEnrollmentsByCourseId(courseId, pageable)
                .map(enrollmentMapper::toEnrollmentDTO);
    }
}
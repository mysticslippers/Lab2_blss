package me.ifmo.backend.controllers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.course.CourseDTO;
import me.ifmo.backend.mappers.CourseMapper;
import me.ifmo.backend.services.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
@Validated
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @GetMapping
    public Page<CourseDTO> getCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DecimalMin(value = "0.0") BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin(value = "0.0") BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean onlyAvailable,
            Pageable pageable
    ) {
        return courseService.getCourses(
                title,
                minPrice,
                maxPrice,
                isActive,
                onlyAvailable,
                pageable
        ).map(courseMapper::toCourseDTO);
    }

    @GetMapping("/{id}")
    public CourseDTO getCourseById(@PathVariable @Min(1) Long id) {
        return courseMapper.toCourseDTO(courseService.getCourseById(id));
    }
}
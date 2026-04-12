package me.ifmo.backend.controllers;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.course.CourseDTO;
import me.ifmo.backend.mappers.CourseMapper;
import me.ifmo.backend.services.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
@Validated
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @GetMapping
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        return courseService.getAllCourses(pageable)
                .map(courseMapper::toCourseDTO);
    }

    @GetMapping("/available")
    public Page<CourseDTO> getAllAvailableCourses(Pageable pageable) {
        return courseService.getAllAvailableCourses(pageable)
                .map(courseMapper::toCourseDTO);
    }

    @GetMapping("/{id}")
    public CourseDTO getCourseById(@PathVariable @Min(1) Long id) {
        return courseMapper.toCourseDTO(courseService.getCourseById(id));
    }
}
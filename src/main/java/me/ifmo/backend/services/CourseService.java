package me.ifmo.backend.services;

import me.ifmo.backend.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    Page<Course> getAllCourses(Pageable pageable);

    Page<Course> getAllAvailableCourses(Pageable pageable);

    Course getCourseById(Long id);

    boolean isCourseAvailableForEnrollment(Long courseId);
}
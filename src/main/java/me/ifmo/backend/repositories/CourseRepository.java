package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllByIsActiveTrue(Pageable pageable);

    @Query("""
            SELECT course
            FROM Course course
            WHERE course.isActive = TRUE
              AND course.availablePlaces > 0
            """)
    Page<Course> findAllAvailableCourses(Pageable pageable);
}
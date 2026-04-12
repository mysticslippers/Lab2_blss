package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  List<Enrollment> findAllByUserId(Long userId);

  List<Enrollment> findAllByCourseId(Long courseId);
}
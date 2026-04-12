package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  Page<Enrollment> findAllByUserId(Long userId, Pageable pageable);

  Page<Enrollment> findAllByCourseId(Long courseId, Pageable pageable);

  List<Enrollment> findAllByStatusAndPaymentExpiresAtBefore(
          EnrollmentStatus status,
          LocalDateTime time
  );
}
package eagle.dev.houseofculture.enrollment.repository

import eagle.dev.houseofculture.enrollment.model.OldEnrollment
import org.springframework.data.jpa.repository.JpaRepository

interface OldEnrollmentRepository: JpaRepository<OldEnrollment, Long> {}
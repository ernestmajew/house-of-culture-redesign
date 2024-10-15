package eagle.dev.houseofculture.enrollment.repository

import eagle.dev.houseofculture.enrollment.model.Payment
import eagle.dev.houseofculture.enrollment.model.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PaymentRepository: JpaRepository<Payment, Long> {


    @Query("select p from Payment p " +
            "where p.status = 'PENDING' OR p.status = 'NEW'")
    fun findByStatusPendingOrNew(): List<Payment>



}
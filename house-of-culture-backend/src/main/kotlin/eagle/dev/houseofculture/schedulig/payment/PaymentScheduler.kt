package eagle.dev.houseofculture.schedulig.payment

import eagle.dev.houseofculture.enrollment.repository.PaymentRepository
import eagle.dev.houseofculture.payu.service.PayUOrderService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class PaymentScheduler(
    private val paymentRepository: PaymentRepository,
    private val payUOrderService: PayUOrderService
) {
    val logger = LoggerFactory.getLogger(this::class.java)


    //every 5 minutes
    @Scheduled(cron = "0 */2 * ? * *")
    fun updatePaymentsStatus() {
        logger.info("CRON JOB STARTED: Update payment status")
        val payments = paymentRepository.findByStatusPendingOrNew();

        payments.mapNotNull { payment ->
            payment.payuId?.let {
                val newStatus = payUOrderService.getOrderStatus(it)
                if(newStatus == payment.status) null
                else {
                    payment.status = newStatus
                    payment
                }
            }
        }.let(paymentRepository::saveAll)
    }

}
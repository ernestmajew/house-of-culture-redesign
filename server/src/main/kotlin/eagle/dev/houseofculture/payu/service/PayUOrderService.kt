package eagle.dev.houseofculture.payu.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonParser
import eagle.dev.houseofculture.enrollment.model.Payment
import eagle.dev.houseofculture.enrollment.model.PaymentStatus
import eagle.dev.houseofculture.exceptions.InternalServerError
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.payu.config.PayUConfigurationProperties
import eagle.dev.houseofculture.payu.model.*
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*


@Service
class PayUOrderService(
    private val payUConfiguration: PayUConfigurationProperties,
    @Resource(name = "payuApiRestTemplate") private val restTemplate: RestTemplate,
    @Value("\${app.url.frontend}") private val frontendUrl: String,
    @Value("\${app.institution.name}") private val institutionName: String
) {
    val plnCurrencyCode = "PLN"
    val logger = LoggerFactory.getLogger(this::class.java)

    // creates PayU order based on application Payment. Returns redirectUri
    fun createOrder(payment: Payment, customerIp: String): String {
        val orderAmount = (payment.amount*100).toInt().toString() // in polish grosze

        val response = this.order(
            OrderCreateRequest(
                continueUrl = frontendUrl, // TODO: add frontend view with payment confirmation
                extOrderId = payment.id.toString(),
                customerIp = customerIp,
                merchantPosId = payUConfiguration.merchantPosId!!,
                description = payUConfiguration.description!!,
                currencyCode = plnCurrencyCode,
                totalAmount = (payment.amount*100).toInt().toString(),
                products = listOf(
                    Product(
                        name = "Opłata za zajęcia w $institutionName",
                        quantity = "1",
                        unitPrice = orderAmount
                    )
                ),
                buyer = Buyer(
                    email = payment.buyer.email,
                    language = "pl"
                )
            )
        )

        if (response.status.statusCode != Status.SUCCESS)
            throw InternalServerError("Cannot create order for payment id: ${payment.id}")

        payment.payuId = response.orderId

        logger.info("Successfully create order for payment id: ${payment.id}")

        return response.redirectUri
    }

    fun getOrderStatus(orderId: String): PaymentStatus{
        val jsonResponse = restTemplate.getForEntity(
            "${payUConfiguration.orderUrl}/$orderId",
            String::class.java
        )

        val jsonObject = JsonParser.parseString(jsonResponse.body).asJsonObject
        val orderObject = jsonObject.getAsJsonArray("orders").get(0).asJsonObject
        val status = orderObject.get("status").asString
        return PaymentStatus.valueOf(status)
    }

    private fun order(orderCreateRequest: OrderCreateRequest): OrderCreateResponse {
        orderCreateRequest.continueUrl = "$frontendUrl/payment/continue/${orderCreateRequest.extOrderId}"
        //for now nothing will be done with this url
        val jsonResponse = restTemplate.postForEntity(
            payUConfiguration.orderUrl!!, orderCreateRequest,
            String::class.java
        )
        return ObjectMapper().readValue(jsonResponse.body, OrderCreateResponse::class.java)
    }

}
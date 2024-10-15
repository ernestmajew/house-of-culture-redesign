package eagle.dev.houseofculture.payu.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "payu")
class PayUConfigurationProperties {
   var description: String? = null
   var clientId: String? = null
   var clientSecret: String? = null
   var authorizationUri: String? = null
   var merchantPosId: String? = null
   var orderUrl: String? = null
}
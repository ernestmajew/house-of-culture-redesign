package eagle.dev.houseofculture.payu.model


data class OrderCreateRequest(
   var continueUrl: String = "",
   var customerIp: String,
   var merchantPosId: String,
   var description: String,
   var currencyCode: String,
   var totalAmount: String,
   var buyer: Buyer,
   var products: List<Product>,
   var extOrderId: String
)
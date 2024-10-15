package eagle.dev.houseofculture.payu.model


data class OrderCreateResponse (
    var orderId: String = "",
    var status: Status = Status(""),
    var redirectUri: String = "",
    var extOrderId: String? = null,
   ){}

data class Status(
    var statusCode: String = "",
){
    companion object {
        const val SUCCESS = "SUCCESS"

    }
}
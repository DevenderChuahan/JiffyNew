package `in`.jiffycharge.gopower.model

data class ApplePaymentBO(
    val amount: String,
    val currency: String,
    val orderId: String,
    val serverCallBackUrl: String
)
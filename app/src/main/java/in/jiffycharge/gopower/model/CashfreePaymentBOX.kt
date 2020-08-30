package `in`.jiffycharge.gopower.model

data class CashfreePaymentBOX(
    val appId: String,
    val customerEmail: String,
    val customerPhone: String,
    val notifyUrl: String,
    val orderAmount: String,
    val orderCurrency: String,
    val orderId: String,
    val token: String
)
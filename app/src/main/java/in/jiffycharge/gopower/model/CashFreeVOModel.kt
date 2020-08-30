package `in`.jiffycharge.gopower.model

data class CashFreeVOModel(
    val orderAmount: String,
    val orderId: String,
    val paymentMode: String,
    val referenceId: String,
    val signature: String,
    val txMsg: String,
    val txStatus: String,
    val txTime: String
)
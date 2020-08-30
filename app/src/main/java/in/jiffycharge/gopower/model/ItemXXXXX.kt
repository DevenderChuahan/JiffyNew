package `in`.jiffycharge.gopower.model

data class ItemXXXXX(
    val alipaPpaySignature: String,
    val applePaymentBO: ApplePaymentBO,
    val cashfreePaymentBO: CashfreePaymentBO,
    val fondyPaymetBO: FondyPaymetBO,
    val sn: String,
    val weiXinPaymet: WeiXinPaymet
)
package `in`.jiffycharge.gopower.model

data class WeiXinPaymetX(
    val appId: String,
    val nonceStr: String,
    val package1: String,
    val partnerId: String,
    val prepayId: String,
    val sign: String,
    val timeStamp: String
)
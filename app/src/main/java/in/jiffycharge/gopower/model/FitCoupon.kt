package `in`.jiffycharge.gopower.model

data class FitCoupon(
    val amount: Int,
    val cityId: String,
    val cityName: String,
    val currency: String,
    val expireDate: String,
    val id: Int,
    val source: String
)
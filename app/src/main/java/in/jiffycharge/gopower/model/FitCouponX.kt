package `in`.jiffycharge.gopower.model

import java.io.Serializable

data class FitCouponX(
    val amount: Int,
    val cityId: String,
    val cityName: String,
    val currency: String,
    val expireDate: String,
    val id: Int,
    val source: String
):Serializable
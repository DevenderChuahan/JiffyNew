package `in`.jiffycharge.gopower.model

data class PortableBattery(
    val adapter: Boolean,
    val battType: String,
    val cabinetChannel: Int,
    val cable: String,
    val colorId: String,
    val electricity: Int,
    val isdamage: String,
    val lastLocationTime: String,
    val portableBatteryCode: String,
    val temperature: String,
    val voltage: Int
)
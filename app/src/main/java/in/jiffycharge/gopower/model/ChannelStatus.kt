package `in`.jiffycharge.gopower.model

data class ChannelStatus(
    val channel: Int,
    val isButton: Boolean,
    val isReadId: Boolean,
    val leftOrigin: Boolean,
    val lock: Boolean,
    val portableBattery: PortableBattery,
    val rightOrigin: Boolean
)
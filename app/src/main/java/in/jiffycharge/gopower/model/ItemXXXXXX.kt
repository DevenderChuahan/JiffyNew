package `in`.jiffycharge.gopower.model

data class ItemXXXXXX(
    val batteryType2Count: Int,
    val batteryType3Count: Int,
    val batteryType4Count: Int,
    val cabinetAlias: String,
    val cabinetCode: String,
    val channelStatusList: List<ChannelStatus>,
    val chargeRuleDesc: String,
    val existPositionNum: Int,
    val idlePositionNum: Int,
    val lastHeartbeat: String,
    val locationAddress: String,
    val locationDesc: String,
    val locationLat: Double,
    val locationLon: Double,
    val online: Boolean,
    val positionTotal: Int,
    val seller: Seller,
    val sysCode: String,
    val typeId: Int
)
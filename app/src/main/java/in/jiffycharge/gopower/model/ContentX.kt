package `in`.jiffycharge.gopower.model

data class ContentX(
    val createTime: Long,
    val isDeleted: Boolean,
    val logAmount: Int,
    val logInfo: String,
    val logType: String,
    val logUserId: Int
)
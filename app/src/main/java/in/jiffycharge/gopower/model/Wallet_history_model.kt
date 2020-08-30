package `in`.jiffycharge.gopower.model


data class Wallet_history_model(
    val content: List<ContentX>,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val searchDate: Long,
    val size: Int,
    val success: Boolean,
    val totalElements: Int,
    val totalPages: Int
)
package `in`.jiffycharge.gopower.model

data class DepositLogModel(
    val content: List<ContentXX>,
    val error: String,
    val error_description: String,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val searchDate: String,
    val size: Int,
    val success: Boolean,
    val totalElements: Int,
    val totalPages: Int
)
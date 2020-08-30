package `in`.jiffycharge.gopower.model

data class Order_list_model(
    val content: List<Content>,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val searchDate: Long,
    val size: Int,
    val success: Boolean,
    val totalElements: Int,
    val totalPages: Int
)
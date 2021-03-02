package `in`.jiffycharge.gopower.model

data class MemberCouponListModel(
    val content: List<ContentXXX>,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val searchDate: Long,
    val size: Int,
    val success: Boolean,
    val totalElements: Int,
    val totalPages: Int,
    val error_description: String

)
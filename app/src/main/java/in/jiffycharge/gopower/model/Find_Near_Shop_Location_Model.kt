package `in`.jiffycharge.gopower.model

data class Find_Near_Shop_Location_Model(
    val error: String,
    val error_description: String,
    val items: List<ItemXXX>,
    val success: Boolean
)
package `in`.jiffycharge.gopower.model

data class ItemXXXXXXX(
    val beginLocationDetails: String,
    val beginLocationLat: Int,
    val beginLocationLon: Int,
    val beginTime: String,
    val borrowCabinetId: Int,
    val borrowCabinetStatusCode: String,
    val borrowChannel: Int,
    val borrowSellerId: Int,
    val borrowSysCode: String,
    val chargeRuleDesc: String,
    val checkDesc: String,
    val checkName: String,
    val checkTime: String,
    val cityId: String,
    val clientSource: String,
    val country: String,
    val createTime: String,
    val currency: String,
    val doingFeedbacks: List<DoingFeedback>,
    val endLocaitonDetails: String,
    val endLocationLat: Int,
    val endLocationLon: Int,
    val exchangeRateDesc: String,
    val finishTime: String,
    val fitCoupon: FitCoupon,
    val id: Int,
    val isDeleted: Boolean,
    val officeId: Int,
    val orderCode: String,
    val outOrderId: String,
    val payOrderCode: String,
    val payPrice: Int,
    val payTime: String,
    val payType: String,
    val paymentMark: String,
    val pbCode: String,
    val pbId: Int,
    val price: Int,
    val ratePrice: Int,
    val returnCabinetId: Int,
    val returnChannel: Int,
    val returnSellerId: Int,
    val returnSysCode: String,
    val returnType: String,
    val rideTime: Int,
    val sellerName: String,
    val status: Int,
    val uid: Int,
    val userCouponId: Int
)
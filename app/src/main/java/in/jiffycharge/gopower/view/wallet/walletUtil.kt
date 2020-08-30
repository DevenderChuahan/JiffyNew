package `in`.jiffycharge.gopower.view.wallet

import androidx.annotation.DrawableRes

class WallerPayBean(@DrawableRes val logo: Int,
                    val text: String,
                    var hint: String?,
                    var mark: WallerPayMark,
                    var isSelect: Boolean)

enum class WallerPayMark(val markStr: String) {
    BALANCE("balance"),
    CASHFREE("cashfree"),
    STRIPE("stripe"),
    FONDY("fondy"),
    DEFAULT("cashfree");
}
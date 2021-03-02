package `in`.jiffycharge.gopower.payment

import `in`.jiffycharge.gopower.model.CashfreeModel
import `in`.jiffycharge.gopower.model.ItemXXXXX
import android.app.Activity

class PayResultActivity(val context: Activity) {

companion object
{
    private const val PARAM_DEPOSIT = "deposit"
    private const val PAY_ORDER = "payorder"
    private const val RE_CHARGE = "recharge"

    private const val BASE_RETURN_URL = "scheme://gopower/pay/cashfree?type="
    const val RETURN_URL_DEPOSIT = BASE_RETURN_URL + PARAM_DEPOSIT
    const val RETURN_URL_PAY_ORDER = BASE_RETURN_URL + PAY_ORDER
    const val RETURN_URL_RE_CHARGE = BASE_RETURN_URL + RE_CHARGE

}

    fun checkPayResult(payResult: CashfreeModel, payResultCall:(Boolean)->Unit)
    {
        val cashFreeBo=payResult.item.cashfreePaymentBO?:return
        if (cashFreeBo != null)
        {

            Payutils.payByCashFree(context, hashMapOf(
                "appId" to cashFreeBo.appId,
                "orderId" to cashFreeBo.orderId,
                "orderAmount" to cashFreeBo.orderAmount,
//                "orderAmount" to "1",
                "customerPhone" to cashFreeBo.customerPhone,
//                "notifyUrl" to cashFreeBo.notifyUrl,
                "orderCurrency" to cashFreeBo.orderCurrency,
                "customerEmail" to cashFreeBo.customerEmail
            ),cashFreeBo.token,object:PayResultListener
            {
                override fun onSuccess(result: Any?) {
                    payResultCall.invoke(true)
                }

                override fun onCancel() {
                    super.onCancel()
                    payResultCall.invoke(false)

                }

                override fun onFail(error: String?) {
                    super.onFail(error)
                    payResultCall.invoke(false)

                }


            })
            return



        }
        payResultCall.invoke(false)




    }


}